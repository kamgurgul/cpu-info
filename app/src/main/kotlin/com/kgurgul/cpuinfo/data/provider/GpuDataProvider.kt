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

    /**
     * Obtain Vulkan version
     */
    fun getVulkanVersion(): String {
        val default = resources.getString(R.string.unknown)
        if (Build.VERSION.SDK_INT < 24) {
            return default
        }

        val vulkan = packageManager.systemAvailableFeatures.find {
            it.name == PackageManager.FEATURE_VULKAN_HARDWARE_VERSION
        }?.version ?: 0
        if (vulkan == 0) {
            return default
        }

        // Extract versions from bit field
        // See: https://developer.android.com/reference/android/content/pm/PackageManager#FEATURE_VULKAN_HARDWARE_VERSION
        val major = vulkan shr 22           // Higher 10 bits
        val minor = vulkan shl 10 shr 22    // Middle 10 bits
        val patch = vulkan shl 20 shr 22    // Lower 12 bits
        //
        return "$major.$minor.$patch"
    }
}
