package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
actual class GpuDataProvider actual constructor() : KoinComponent {

    actual fun getGlEsVersion(): String {
        return ""
    }

    actual fun getVulkanVersion(): String {
        return ""
    }

    actual fun getMetalVersion() = ""
}
