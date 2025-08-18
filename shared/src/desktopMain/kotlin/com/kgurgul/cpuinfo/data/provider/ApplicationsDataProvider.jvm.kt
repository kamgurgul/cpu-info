package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class ApplicationsDataProvider actual constructor() :
    IApplicationsDataProvider,
    KoinComponent {

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
                    appIconUri = "",
                )
            }
    }

    actual override fun areApplicationsSupported() = true

    actual override fun hasSystemAppsFiltering() = false

    actual override fun hasAppManagementSupported() = false

    actual override fun hasManualRefresh() = true
}
