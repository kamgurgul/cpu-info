/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.utils.uninstall

import com.kgurgul.cpuinfo.domain.model.UninstallOutcome
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Kernel32Util
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.Shell32
import com.sun.jna.platform.win32.ShellAPI
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinError
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions
import java.io.File
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

internal data class WindowsUninstallCommand(
    val executable: String,
    val parameters: String,
    val isMsi: Boolean = false,
)

/** Keep the executable separate from its parameters; never wrap registry data in cmd.exe. */
internal fun windowsUninstallCommand(
    entry: WindowsAppEntry,
    systemDirectory: String,
    expandEnvironment: (String) -> String = { it },
): WindowsUninstallCommand {
    require(entry.canUninstall) { "Application does not support removal" }
    // A patch-removal command must never become removal of the entire product.
    require(entry.uninstallCommand?.contains("MSIPATCHREMOVE", ignoreCase = true) != true) {
        "MSI patch removal is unsupported"
    }
    if (entry.windowsInstaller && isProductCode(entry.target.key)) {
        return msiCommand(systemDirectory, entry.target.key)
    }
    val command = expandEnvironment(requireNotNull(entry.uninstallCommand)).trim()
    require(command.none { it == '\u0000' || it == '\n' || it == '\r' }) {
        "Invalid uninstall command"
    }
    val executable: String
    val parameters: String
    val bareMsi = Regex("""(?i)^msiexec(?:\.exe)?(?=\s|/)""").find(command)
    if (bareMsi != null) {
        executable = "msiexec.exe"
        parameters = command.substring(bareMsi.range.last + 1).trimStart()
    } else if (command.startsWith('"')) {
        val end = command.indexOf('"', 1)
        require(end > 1) { "Unterminated executable path" }
        executable = command.substring(1, end)
        require(end == command.lastIndex || command[end + 1].isWhitespace()) {
            "Invalid executable path"
        }
        parameters = command.substring(end + 1).trimStart()
    } else {
        val end =
            Regex("(?i)\\.exe(?=\\s|$)").find(command)?.range?.last?.plus(1)
                ?: throw IllegalArgumentException("Expected an executable uninstall command")
        executable = command.substring(0, end)
        parameters = command.substring(end).trimStart()
    }
    val executableName = executable.replace('/', '\\').substringAfterLast('\\')
    if (executableName.equals("msiexec.exe", true) || executableName.equals("msiexec", true)) {
        // Only normalize product removal. A patch-removal /I command must not become /X.
        require(!parameters.contains("MSIPATCHREMOVE", ignoreCase = true)) {
            "MSI patch removal is unsupported"
        }
        val product =
            Regex("""(?i)^/[ix]\s*("?)(\{[0-9a-f-]+\})\1(?:\s|$)""")
                .find(parameters)
                ?.groupValues
                ?.get(2)
        require(product != null && isProductCode(product)) { "Expected an MSI product code" }
        return msiCommand(systemDirectory, product)
    }
    require(executable.endsWith(".exe", ignoreCase = true) && '"' !in executable) {
        "Expected an executable uninstall command"
    }
    require(Regex("^[a-zA-Z]:\\\\").containsMatchIn(executable) || executable.startsWith("\\\\")) {
        "The uninstaller must have an absolute path"
    }
    return WindowsUninstallCommand(executable, parameters)
}

private fun msiCommand(systemDirectory: String, product: String) =
    WindowsUninstallCommand(
        executable = "${systemDirectory.trimEnd('\\')}\\msiexec.exe",
        parameters = "/x $product /norestart",
        isMsi = true,
    )

internal fun windowsExitOutcome(exitCode: Int, isMsi: Boolean): UninstallOutcome =
    when {
        exitCode == 0 -> UninstallOutcome.REMOVED // Registry removal is verified separately.
        isMsi && exitCode == 1602 -> UninstallOutcome.CANCELLED
        isMsi && (exitCode == 1641 || exitCode == 3010) -> UninstallOutcome.RESTART_REQUIRED
        else -> throw IOException("Uninstaller exited with code $exitCode")
    }

internal class WindowsUninstaller(
    private val registry: WindowsAppRegistry = NativeWindowsAppRegistry(),
    private val launch: suspend (WindowsAppEntry) -> UninstallOutcome = ::launchWindowsUninstaller,
) {
    suspend fun uninstall(target: UninstallTarget.WindowsRegistry): UninstallOutcome {
        // Re-read precisely the selected entry, so refreshes or duplicate names cannot retarget it.
        val entry = registry.read(target) ?: throw IOException("Application is no longer available")
        require(entry.target == target && entry.canUninstall) {
            "Application does not support removal"
        }
        val outcome = launch(entry)
        if (outcome != UninstallOutcome.REMOVED) return outcome
        // Some uninstallers hand off to another process before their own process exits.
        return withTimeoutOrNull(10_000) {
            while (registry.exists(target)) delay(500)
            UninstallOutcome.REMOVED
        } ?: UninstallOutcome.UNCONFIRMED
    }
}

private suspend fun launchWindowsUninstaller(entry: WindowsAppEntry): UninstallOutcome {
    val systemPath = CharArray(WINDOWS_MAX_PATH)
    val size = SystemDirectoryApi.instance.GetSystemDirectory(systemPath, systemPath.size)
    check(size > 0 && size < systemPath.size) { "Cannot locate the Windows system directory" }
    val command =
        windowsUninstallCommand(
            entry,
            String(systemPath, 0, size),
            Kernel32Util::expandEnvironmentStrings,
        )
    require(File(command.executable).isFile) { "Uninstaller does not exist: ${command.executable}" }
    val execution =
        ShellAPI.SHELLEXECUTEINFO().apply {
            fMask = 0x00000040 or 0x00000100 or 0x00000400 // NOCLOSEPROCESS | NOASYNC | FLAG_NO_UI
            lpVerb = "open" // ShellExecute handles manifest-requested elevation through UAC.
            lpFile = command.executable
            lpParameters = command.parameters
            lpDirectory = File(command.executable).parent
            nShow = 1 // SW_SHOWNORMAL
        }
    // Shell extensions may require COM. Initialize and uninitialize on the same IO thread.
    val comResult = Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_APARTMENTTHREADED)
    try {
        if (!Shell32.INSTANCE.ShellExecuteEx(execution)) {
            val error = Kernel32.INSTANCE.GetLastError()
            if (error == WinError.ERROR_CANCELLED) return UninstallOutcome.CANCELLED
            throw Win32Exception(error)
        }
    } finally {
        if (comResult.toInt() >= 0) Ole32.INSTANCE.CoUninitialize()
    }
    val process = execution.hProcess ?: return UninstallOutcome.UNCONFIRMED
    return try {
        // No redirected pipes to fill. Cancellation stops monitoring, never kills an installer.
        withTimeoutOrNull(15 * 60_000L) {
            while (true) {
                when (Kernel32.INSTANCE.WaitForSingleObject(process, 0)) {
                    WinBase.WAIT_OBJECT_0 -> break
                    WinError.WAIT_TIMEOUT -> delay(250)
                    else -> throw Win32Exception(Kernel32.INSTANCE.GetLastError())
                }
            }
            val code = IntByReference()
            if (!Kernel32.INSTANCE.GetExitCodeProcess(process, code)) {
                throw Win32Exception(Kernel32.INSTANCE.GetLastError())
            }
            windowsExitOutcome(code.value, command.isMsi)
        } ?: UninstallOutcome.UNCONFIRMED
    } finally {
        Kernel32.INSTANCE.CloseHandle(process)
    }
}

private const val WINDOWS_MAX_PATH = 260

private interface SystemDirectoryApi : StdCallLibrary {
    fun GetSystemDirectory(buffer: CharArray, size: Int): Int

    companion object {
        val instance: SystemDirectoryApi =
            Native.load(
                "kernel32",
                SystemDirectoryApi::class.java,
                W32APIOptions.UNICODE_OPTIONS,
            )
    }
}
