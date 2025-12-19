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
package com.kgurgul.cpuinfo.domain.action

import com.kgurgul.cpuinfo.features.applications.JvmUninstallNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class ExternalAppAction actual constructor() : IExternalAppAction, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    actual override fun launch(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun openSettings(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun uninstall(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun uninstallWithPath(uninstallerPath: String): Result<Unit> {
        return runCatching {
            val osName = systemInfo.operatingSystem.family.lowercase()
            when {
                osName.contains("windows") -> {
                    executeWindowsUninstaller(uninstallerPath)
                }

                osName.contains("mac") || osName.contains("darwin") -> {
                    executeMacUninstaller(uninstallerPath)
                }

                else -> {
                    // Not supported for other platforms
                }
            }
        }
    }

    private fun executeWindowsUninstaller(uninstallerPath: String) {
        // Handle different uninstall string formats
        // Some are direct executables, some are "MsiExec.exe /X{GUID}" format
        val processBuilder =
            if (uninstallerPath.contains("MsiExec", ignoreCase = true)) {
                ProcessBuilder("cmd", "/c", uninstallerPath)
            } else {
                // For direct executables, quote the path in case it contains spaces
                ProcessBuilder("cmd", "/c", "\"$uninstallerPath\"")
            }
        val process = processBuilder.start()
        scope.launch {
            process.waitFor()
            JvmUninstallNotifier.notifyUninstallCompleted()
        }
    }

    private fun executeMacUninstaller(appPath: String) {
        // Move the app to trash using AppleScript
        val script =
            """
            tell application "Finder"
                delete POSIX file "$appPath"
            end tell
        """
                .trimIndent()
        val process = ProcessBuilder("osascript", "-e", script).start()
        scope.launch {
            process.waitFor()
            JvmUninstallNotifier.notifyUninstallCompleted()
        }
    }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}
