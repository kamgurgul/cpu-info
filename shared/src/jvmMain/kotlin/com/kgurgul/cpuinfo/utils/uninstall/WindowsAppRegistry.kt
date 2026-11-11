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

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinError
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg

internal data class WindowsAppEntry(
    val target: UninstallTarget.WindowsRegistry,
    val name: String,
    val version: String = "",
    val installLocation: String = "",
    val uninstallCommand: String? = null,
    val windowsInstaller: Boolean = false,
    val noRemove: Boolean = false,
    val systemComponent: Boolean = false,
) {
    val canUninstall: Boolean
        get() =
            !noRemove &&
                !systemComponent &&
                (!uninstallCommand.isNullOrBlank() ||
                    (windowsInstaller && isProductCode(target.key)))

    fun toApplicationData(icon: String) =
        ExtendedApplicationData(
            name = name,
            packageName = target.id,
            displayIdentifier = installLocation.ifBlank { name },
            versionName = version,
            nativeLibs = emptyList(),
            hasNativeLibs = false,
            appIconUri = icon,
            uninstallTarget = target.takeIf { canUninstall },
        )
}

internal interface WindowsAppRegistry {
    fun list(): List<WindowsAppEntry>

    fun read(target: UninstallTarget.WindowsRegistry): WindowsAppEntry?

    fun exists(target: UninstallTarget.WindowsRegistry): Boolean
}

/** Reads fresh entries from each registry view; display names are not identifiers. */
internal class NativeWindowsAppRegistry : WindowsAppRegistry {
    override fun list(): List<WindowsAppEntry> = buildList {
        for (hive in UninstallTarget.Hive.entries) {
            for (view in UninstallTarget.RegistryView.entries) {
                // HKCU\Software is shared between views; enumerating it twice duplicates apps.
                if (
                    hive == UninstallTarget.Hive.CURRENT_USER &&
                    view == UninstallTarget.RegistryView.REGISTRY_32
                )
                    continue
                val root = UninstallTarget.WindowsRegistry(hive, view, "")
                val keys =
                    try {
                        if (!Advapi32Util.registryKeyExists(root.hkey, UNINSTALL_KEY, root.access))
                            continue
                        Advapi32Util.registryGetKeys(root.hkey, UNINSTALL_KEY, root.access)
                    } catch (_: Win32Exception) {
                        continue
                    }
                for (key in keys) {
                    try {
                        read(root.copy(key = key))?.let { add(it) }
                    } catch (_: RuntimeException) {
                        // Skip disappearing, inaccessible, or malformed entries.
                    }
                }
            }
        }
    }

    override fun exists(target: UninstallTarget.WindowsRegistry): Boolean =
        Advapi32Util.registryKeyExists(target.hkey, target.path, target.access)

    override fun read(target: UninstallTarget.WindowsRegistry): WindowsAppEntry? {
        if (!exists(target)) return null
        val name = string(target, "DisplayName")?.takeIf { it.isNotBlank() } ?: return null
        return WindowsAppEntry(
            target = target,
            name = name,
            version = string(target, "DisplayVersion").orEmpty(),
            installLocation = string(target, "InstallLocation").orEmpty(),
            // The interactive command lets the vendor handle confirmation and running apps.
            uninstallCommand = string(target, "UninstallString")?.takeIf { it.isNotBlank() },
            windowsInstaller = int(target, "WindowsInstaller") == 1,
            noRemove = int(target, "NoRemove") == 1,
            systemComponent = int(target, "SystemComponent") == 1,
        )
    }

    private fun string(target: UninstallTarget.WindowsRegistry, name: String): String? = optional {
        Advapi32Util.registryGetStringValue(target.hkey, target.path, name, target.access)
    }

    private fun int(target: UninstallTarget.WindowsRegistry, name: String): Int? = optional {
        Advapi32Util.registryGetIntValue(target.hkey, target.path, name, target.access)
    }

    private fun <T> optional(read: () -> T): T? =
        try {
            read()
        } catch (e: Win32Exception) {
            if (e.errorCode == WinError.ERROR_FILE_NOT_FOUND) null else throw e
        }
}

private const val UNINSTALL_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall"
private val UninstallTarget.WindowsRegistry.hkey: WinReg.HKEY
    get() =
        when (hive) {
            UninstallTarget.Hive.LOCAL_MACHINE -> WinReg.HKEY_LOCAL_MACHINE
            UninstallTarget.Hive.CURRENT_USER -> WinReg.HKEY_CURRENT_USER
        }
private val UninstallTarget.WindowsRegistry.access: Int
    get() =
        when (view) {
            UninstallTarget.RegistryView.REGISTRY_64 -> WinNT.KEY_WOW64_64KEY
            UninstallTarget.RegistryView.REGISTRY_32 -> WinNT.KEY_WOW64_32KEY
        }
private val UninstallTarget.WindowsRegistry.path: String
    get() {
        require(key.isNotBlank() && '\\' !in key && '/' !in key) {
            "Invalid uninstall registry key"
        }
        return "$UNINSTALL_KEY\\$key"
    }

internal fun isProductCode(value: String): Boolean =
    Regex("\\{[0-9a-fA-F]{8}-(?:[0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}\\}").matches(value)
