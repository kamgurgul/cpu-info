package com.kgurgul.cpuinfo.data.provider

import android.app.ActivityManager
import android.content.pm.PackageManager
import android.os.Build
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.gles_version
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.vulkan_version
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class GpuDataProvider actual constructor() : IGpuDataProvider, KoinComponent {

    private val activityManager: ActivityManager by inject()
    private val packageManager: PackageManager by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            val vulcanVersion = getVulkanVersion()
            if (vulcanVersion.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.vulkan_version, vulcanVersion))
            }
            val glVersion = getGlEsVersion()
            if (glVersion.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.gles_version, glVersion))
            }
        }
    }

    private fun getGlEsVersion(): String {
        return activityManager.deviceConfigurationInfo.glEsVersion
    }

    private fun getVulkanVersion(): String {
        val default = runBlocking { getString(Res.string.unknown) }
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
        val major = vulkan shr 22 // Higher 10 bits
        val minor = vulkan shl 10 shr 22 // Middle 10 bits
        val patch = vulkan shl 20 shr 22 // Lower 12 bits
        //
        return "$major.$minor.$patch"
    }
}
