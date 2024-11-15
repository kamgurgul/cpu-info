package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData

class FakeApplicationsDataProvider(
    var applicationsSupported: Boolean = false,
    var installedApplications: List<ExtendedApplicationData> = emptyList(),
) : IApplicationsDataProvider {

    override fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData> {
        return installedApplications
    }

    override fun areApplicationsSupported(): Boolean {
        return applicationsSupported
    }
}
