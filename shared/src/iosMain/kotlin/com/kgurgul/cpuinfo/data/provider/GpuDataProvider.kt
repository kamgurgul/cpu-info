package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class GpuDataProvider actual constructor() {

    actual fun getGlEsVersion(): String {
        return "GLEs version"
    }

    actual fun getVulkanVersion(): String {
        return "Vulkan Version"
    }
}
