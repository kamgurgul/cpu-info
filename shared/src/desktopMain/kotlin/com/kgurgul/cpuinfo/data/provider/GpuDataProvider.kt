package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class GpuDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val gpu = systemInfo.hardware.graphicsCards

    actual fun getGlEsVersion(): String {
        return ""
    }

    actual fun getVulkanVersion(): String {
        return ""
    }

    actual fun getMetalVersion() = ""
}
