package com.kgurgul.cpuinfo.data.provider

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import javax.inject.Inject

class ApplicationsDataProvider @Inject constructor(
    private val packageManager: PackageManager
) {

    fun getInstalledApplications(withSystemApps: Boolean): List<ApplicationInfo> {
        val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return if (withSystemApps) {
            applications
        } else {
            applications.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        }
    }
}