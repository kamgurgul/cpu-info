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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import java.io.File
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class ApplicationsDataProvider actual constructor() :
    IApplicationsDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override fun getInstalledApplications(
        withSystemApps: Boolean
    ): List<ExtendedApplicationData> {
        return systemInfo.operatingSystem.installedApplications
            .distinctBy { it.name }
            .map {
                ExtendedApplicationData(
                    name = it.name,
                    packageName = it.name,
                    versionName = it.version ?: "",
                    nativeLibs = emptyList(),
                    hasNativeLibs = false,
                    appIconUri = extractIconPath(it.name, it.additionalInfo),
                )
            }
    }

    private fun extractIconPath(appName: String, additionalInfo: Map<String, String>): String {
        val osName = systemInfo.operatingSystem.family.lowercase()

        return when {
            osName.contains("mac") || osName.contains("darwin") -> {
                extractMacIconPath(appName, additionalInfo)
            }

            osName.contains("windows") -> {
                extractWindowsIconPath(appName, additionalInfo)
            }

            osName.contains("linux") -> {
                extractLinuxIconPath(appName, additionalInfo)
            }

            else -> ""
        }
    }

    private fun extractMacIconPath(appName: String, additionalInfo: Map<String, String>): String {
        val location = additionalInfo["Location"] ?: return ""

        // macOS apps follow bundle structure: AppName.app/Contents/Resources/AppIcon.icns
        // Common icon names: AppIcon.icns, app.icns, or {AppName}.icns
        val appFile = File(location)
        if (!appFile.exists()) return ""

        val resourcesDir = File(appFile, "Contents/Resources")
        if (!resourcesDir.exists()) return ""

        val possibleIconNames =
            listOf(
                "AppIcon.icns",
                "app.icns",
                "${appName}.icns",
                "${appFile.nameWithoutExtension}.icns",
            )

        for (iconName in possibleIconNames) {
            val iconFile = File(resourcesDir, iconName)
            if (iconFile.exists()) {
                return iconFile.absolutePath
            }
        }

        // Fallback: find first .icns file
        resourcesDir
            .listFiles { file -> file.extension == "icns" }
            ?.firstOrNull()
            ?.let {
                return it.absolutePath
            }

        return ""
    }

    private fun extractWindowsIconPath(
        appName: String,
        additionalInfo: Map<String, String>,
    ): String {
        val installLocation = additionalInfo["installLocation"] ?: return ""

        val installDir = File(installLocation)
        if (!installDir.exists()) return ""

        val possibleIconNames = listOf("${appName}.ico", "app.ico", "icon.ico", "${appName}.exe")

        for (iconName in possibleIconNames) {
            val iconFile = File(installDir, iconName)
            if (iconFile.exists() && !isUninstallerExecutable(iconFile)) {
                return iconFile.absolutePath
            }
        }

        // Fallback: find first .ico file
        installDir
            .listFiles { file -> file.extension == "ico" }
            ?.firstOrNull()
            ?.let {
                return it.absolutePath
            }

        // Fallback: find first .exe file (can extract icon from it)
        // Skip uninstaller executables
        installDir
            .listFiles { file -> file.extension == "exe" && !isUninstallerExecutable(file) }
            ?.firstOrNull()
            ?.let {
                return it.absolutePath
            }

        return ""
    }

    private fun isUninstallerExecutable(file: File): Boolean {
        val fileName = file.nameWithoutExtension.lowercase()
        val uninstallerKeywords =
            listOf(
                "uninstall",
                "unins",
                "uninst",
                "remove",
                "cleanup",
                "unwise",
                "setup", // Often setup.exe is used for uninstall
            )
        return uninstallerKeywords.any { keyword -> fileName.contains(keyword) }
    }

    private fun extractLinuxIconPath(
        appName: String,
        @Suppress("UNUSED_PARAMETER") additionalInfo: Map<String, String>,
    ): String {
        // Linux doesn't provide install location in additionalInfo
        // We need to check .desktop files in common locations
        val desktopFileDirs =
            listOf(
                File("/usr/share/applications"),
                File("/usr/local/share/applications"),
                File(System.getProperty("user.home"), ".local/share/applications"),
            )

        for (dir in desktopFileDirs) {
            if (!dir.exists()) continue

            // Try to find .desktop file matching app name
            val desktopFile =
                dir.listFiles { file ->
                        file.extension == "desktop" &&
                            file.nameWithoutExtension.contains(appName, ignoreCase = true)
                    }
                    ?.firstOrNull()

            if (desktopFile != null) {
                // Parse .desktop file to find Icon= entry
                try {
                    val iconPath =
                        desktopFile
                            .readLines()
                            .firstOrNull { it.startsWith("Icon=") }
                            ?.substringAfter("Icon=")
                            ?.trim()

                    if (!iconPath.isNullOrEmpty()) {
                        // Icon can be either absolute path or icon name
                        return if (iconPath.startsWith("/")) {
                            iconPath
                        } else {
                            // Icon name - try to find in icon themes
                            findIconInThemes(iconPath)
                        }
                    }
                } catch (_: Exception) {
                    // Ignore and continue
                }
            }
        }

        return ""
    }

    private fun findIconInThemes(iconName: String): String {
        // Common icon paths in Linux
        val iconDirs =
            listOf(
                "/usr/share/icons/hicolor/48x48/apps",
                "/usr/share/icons/hicolor/64x64/apps",
                "/usr/share/icons/hicolor/128x128/apps",
                "/usr/share/icons/hicolor/scalable/apps",
                "/usr/share/pixmaps",
            )

        val iconExtensions = listOf("png", "svg", "xpm")

        for (dir in iconDirs) {
            val iconDir = File(dir)
            if (!iconDir.exists()) continue

            for (ext in iconExtensions) {
                val iconFile = File(iconDir, "$iconName.$ext")
                if (iconFile.exists()) {
                    return iconFile.absolutePath
                }
            }
        }

        return ""
    }

    actual override fun areApplicationsSupported() = true

    actual override fun hasSystemAppsFiltering() = false

    actual override fun hasAppManagementSupported() = false

    actual override fun hasManualRefresh() = true
}
