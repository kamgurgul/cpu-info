package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class GpuDataProvider() {

    fun getGlEsVersion(): String

    fun getVulkanVersion(): String

    fun getMetalVersion(): String
}