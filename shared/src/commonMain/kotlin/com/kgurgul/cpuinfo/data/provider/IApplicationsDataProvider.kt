package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData

interface IApplicationsDataProvider {

    fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData>

    fun areApplicationsSupported(): Boolean
}
