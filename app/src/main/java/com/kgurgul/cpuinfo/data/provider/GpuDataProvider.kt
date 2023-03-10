package com.kgurgul.cpuinfo.data.provider

import android.app.ActivityManager
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import com.kgurgul.cpuinfo.R
import javax.inject.Inject

class GpuDataProvider @Inject constructor(
    private val activityManager: ActivityManager,
    private val packageManager: PackageManager,
    private val resources: Resources,
) {

    fun getGlEsVersion(): String {
        return activityManager.deviceConfigurationInfo.glEsVersion
    }

    fun getVulkanVersion(): String {
        val unknown = resources.getString(R.string.unknown)
        if (Build.VERSION.SDK_INT < 24) {
            return unknown
        }
        if (packageManager.hasSystemFeature(
                PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,
                VULKAN_1_3
            )
        ) {
            return "1.3"
        }
        if (packageManager.hasSystemFeature(
                PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,
                VULKAN_1_2
            )
        ) {
            return "1.2"
        }
        if (packageManager.hasSystemFeature(
                PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,
                VULKAN_1_1
            )
        ) {
            return "1.1"
        }
        return if (packageManager.hasSystemFeature(
                PackageManager.FEATURE_VULKAN_HARDWARE_VERSION,
                VULKAN_1_0
            )
        ) {
            "1.0"
        } else {
            unknown
        }
    }

    companion object {
        private const val VULKAN_1_0 = 0x400000
        private const val VULKAN_1_1 = 0x401000
        private const val VULKAN_1_2 = 0x402000
        private const val VULKAN_1_3 = 0x403000
    }
}