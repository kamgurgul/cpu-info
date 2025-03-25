package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData

expect class ApplicationsDataProvider() : IApplicationsDataProvider {

    override fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData>

    override fun areApplicationsSupported(): Boolean

    override fun hasSystemAppsFiltering(): Boolean

    override fun hasAppManagementSupported(): Boolean
}
