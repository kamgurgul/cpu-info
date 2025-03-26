package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData

class FakeApplicationsDataProvider(
    var applicationsSupported: Boolean = false,
    var installedApplications: List<ExtendedApplicationData> = emptyList(),
    var hasSystemAppsFiltering: Boolean = false,
    var hasAppManagementSupported: Boolean = false,
    var hasManualRefresh: Boolean = false,
) : IApplicationsDataProvider {

    override fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData> {
        return installedApplications
    }

    override fun areApplicationsSupported(): Boolean {
        return applicationsSupported
    }

    override fun hasSystemAppsFiltering(): Boolean {
        return hasSystemAppsFiltering
    }

    override fun hasAppManagementSupported(): Boolean {
        return hasAppManagementSupported
    }

    override fun hasManualRefresh(): Boolean {
        return hasManualRefresh
    }
}
