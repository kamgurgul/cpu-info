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
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.io.File
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class ApplicationsDataProvider actual constructor() :
    IApplicationsDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    private data class WindowsUninstallInfo(
        val displayName: String,
        val uninstallString: String?,
        val quietUninstallString: String?,
        val installLocation: String?,
    )

    // Cache for Windows registry uninstall info to avoid repeated lookups
    private var windowsUninstallCache: Map<String, WindowsUninstallInfo>? = null
    private var windowsUninstallCacheByLocation: Map<String, WindowsUninstallInfo>? = null

    actual override fun getInstalledApplications(
        withSystemApps: Boolean
    ): List<ExtendedApplicationData> {
        // Clear cache to get fresh registry data on refresh
        windowsUninstallCache = null
        windowsUninstallCacheByLocation = null

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
                    uninstallerPath = extractUninstallerPath(it.name, it.additionalInfo),
                )
            }
    }

    private fun extractUninstallerPath(
        appName: String,
        additionalInfo: Map<String, String>,
    ): String? {
        val osName = systemInfo.operatingSystem.family.lowercase()

        return when {
            osName.contains("windows") -> {
                extractWindowsUninstallerPath(appName, additionalInfo)
            }

            osName.contains("mac") || osName.contains("darwin") -> {
                extractMacUninstallerPath(additionalInfo)
            }

            osName.contains("linux") -> {
                extractLinuxUninstallerPath(additionalInfo)
            }

            else -> null
        }
    }

    private fun extractWindowsUninstallerPath(
        appName: String,
        additionalInfo: Map<String, String>,
    ): String? {
        // First try to get uninstaller from Windows registry (same source as Control Panel)
        val registryUninstaller = findUninstallerInRegistry(appName, additionalInfo)
        if (registryUninstaller != null) {
            return registryUninstaller
        }

        // Fallback to file-based search if registry lookup fails
        val installLocation = additionalInfo["installLocation"] ?: return null
        val installDir = File(installLocation)
        if (!installDir.exists()) return null

        val uninstallerNames =
            listOf(
                "uninstall.exe",
                "unins000.exe",
                "uninst.exe",
                "uninstaller.exe",
                "Uninstall.exe",
            )

        for (name in uninstallerNames) {
            val uninstaller = File(installDir, name)
            if (uninstaller.exists()) {
                return uninstaller.absolutePath
            }
        }

        // Look for any executable that looks like an uninstaller
        installDir
            .listFiles { file -> file.extension == "exe" && isUninstallerExecutable(file) }
            ?.firstOrNull()
            ?.let {
                return it.absolutePath
            }

        return null
    }

    /**
     * Reads uninstaller information directly from Windows registry. This is the same source that
     * Control Panel's "Programs and Features" uses.
     */
    private fun findUninstallerInRegistry(
        appName: String,
        additionalInfo: Map<String, String>,
    ): String? {
        // Build cache if not already built
        if (windowsUninstallCache == null) {
            buildWindowsUninstallCache()
        }

        val cache = windowsUninstallCache ?: return null
        val cacheByLocation = windowsUninstallCacheByLocation ?: emptyMap()

        // First try exact name match (case-insensitive)
        val normalizedAppName = appName.lowercase()
        val byName = cache[normalizedAppName]
        if (byName != null) {
            val uninstallString = byName.quietUninstallString ?: byName.uninstallString
            if (!uninstallString.isNullOrBlank()) {
                return uninstallString
            }
        }

        // Try matching by install location
        val installLocation = additionalInfo["installLocation"]?.lowercase()?.trimEnd('\\', '/')
        if (installLocation != null) {
            val byLocation = cacheByLocation[installLocation]
            if (byLocation != null) {
                val uninstallString = byLocation.quietUninstallString ?: byLocation.uninstallString
                if (!uninstallString.isNullOrBlank()) {
                    return uninstallString
                }
            }
        }

        // Try partial name matching for cases where registry name differs slightly
        for ((registryName, info) in cache) {
            if (
                normalizedAppName.contains(registryName) || registryName.contains(normalizedAppName)
            ) {
                val uninstallString = info.quietUninstallString ?: info.uninstallString
                if (!uninstallString.isNullOrBlank()) {
                    return uninstallString
                }
            }
        }

        return null
    }

    /**
     * Builds a cache of all uninstall info from Windows registry. This is done once and reused for
     * all apps.
     */
    private fun buildWindowsUninstallCache() {
        val byName = mutableMapOf<String, WindowsUninstallInfo>()
        val byLocation = mutableMapOf<String, WindowsUninstallInfo>()

        // Registry paths where uninstall info is stored (same as Control Panel uses)
        val registryPaths =
            listOf(
                WinReg.HKEY_LOCAL_MACHINE to
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
                WinReg.HKEY_LOCAL_MACHINE to
                    "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
                WinReg.HKEY_CURRENT_USER to
                    "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
            )

        for ((hive, path) in registryPaths) {
            try {
                if (!Advapi32Util.registryKeyExists(hive, path)) continue

                val subKeys = Advapi32Util.registryGetKeys(hive, path)
                for (subKey in subKeys) {
                    val fullPath = "$path\\$subKey"
                    try {
                        val info = readUninstallRegistryEntry(hive, fullPath) ?: continue

                        // Index by normalized display name
                        val normalizedName = info.displayName.lowercase()
                        if (!byName.containsKey(normalizedName)) {
                            byName[normalizedName] = info
                        }

                        // Index by normalized install location
                        val location = info.installLocation?.lowercase()?.trimEnd('\\', '/')
                        if (location != null && !byLocation.containsKey(location)) {
                            byLocation[location] = info
                        }
                    } catch (_: Exception) {
                        // Skip entries we can't read
                    }
                }
            } catch (_: Exception) {
                // Skip registry paths we can't access
            }
        }

        windowsUninstallCache = byName
        windowsUninstallCacheByLocation = byLocation
    }

    private fun readUninstallRegistryEntry(hive: WinReg.HKEY, path: String): WindowsUninstallInfo? {
        return try {
            val displayName =
                try {
                    Advapi32Util.registryGetStringValue(hive, path, "DisplayName")
                } catch (_: Exception) {
                    return null // DisplayName is required
                }

            val uninstallString =
                try {
                    Advapi32Util.registryGetStringValue(hive, path, "UninstallString")
                } catch (_: Exception) {
                    null
                }

            val quietUninstallString =
                try {
                    Advapi32Util.registryGetStringValue(hive, path, "QuietUninstallString")
                } catch (_: Exception) {
                    null
                }

            val installLocation =
                try {
                    Advapi32Util.registryGetStringValue(hive, path, "InstallLocation")
                } catch (_: Exception) {
                    null
                }

            WindowsUninstallInfo(
                displayName = displayName,
                uninstallString = uninstallString,
                quietUninstallString = quietUninstallString,
                installLocation = installLocation,
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun extractMacUninstallerPath(additionalInfo: Map<String, String>): String? {
        // macOS apps are bundles - return the app location for trash-based uninstall
        val location = additionalInfo["Location"]
        if (!location.isNullOrEmpty() && File(location).exists()) {
            return location
        }
        return null
    }

    private fun extractLinuxUninstallerPath(
        @Suppress("UNUSED_PARAMETER") additionalInfo: Map<String, String>
    ): String? {
        // Linux package managers handle uninstallation differently
        // We don't provide direct uninstaller paths for Linux
        return null
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

    actual override fun hasExpandedAppManagementSupported() = false

    actual override fun hasManualRefresh() = true
}
