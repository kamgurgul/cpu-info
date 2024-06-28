package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import org.koin.core.annotation.Factory

@Factory
expect class ApplicationsDataProvider() {

    fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData>

    fun areApplicationsSupported(): Boolean
}