package com.kgurgul.cpuinfo.data.provider

import android.app.ActivityManager
import javax.inject.Inject

class GpuDataProvider @Inject constructor(
        private val activityManager: ActivityManager
) {

    fun getGlEsVersion(): String {
        return activityManager.deviceConfigurationInfo.glEsVersion
    }
}