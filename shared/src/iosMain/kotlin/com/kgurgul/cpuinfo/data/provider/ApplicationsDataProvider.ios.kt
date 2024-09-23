package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import org.koin.core.annotation.Factory

@Factory
actual class ApplicationsDataProvider actual constructor() : IApplicationsDataProvider {

    actual override fun getInstalledApplications(
        withSystemApps: Boolean
    ): List<ExtendedApplicationData> {
        return emptyList()
    }

    actual override fun areApplicationsSupported() = false
}