package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual override fun initLibrary() {
    }

    actual override fun getCpuName(): String {
        return processor.processorIdentifier.name
    }

    actual override fun hasArmNeon(): Boolean {
        return processor.featureFlags.contains("neon")
    }

    actual override fun getL1dCaches(): IntArray? {
        return null
    }

    actual override fun getL1iCaches(): IntArray? {
        return null
    }

    actual override fun getL2Caches(): IntArray? {
        return null
    }

    actual override fun getL3Caches(): IntArray? {
        return null
    }

    actual override fun getL4Caches(): IntArray? {
        return null
    }

    actual override fun getNumberOfCores(): Int {
        return processor.physicalProcessorCount
    }
}
