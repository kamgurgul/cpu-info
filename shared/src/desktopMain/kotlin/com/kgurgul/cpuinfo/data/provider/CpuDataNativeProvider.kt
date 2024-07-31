package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Single(createdAtStart = true)
actual class CpuDataNativeProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual fun initLibrary() {

    }

    actual fun getCpuName(): String {
        return processor.processorIdentifier.name
    }

    actual fun hasArmNeon(): Boolean {
        return processor.featureFlags.contains("neon")
    }

    actual fun getL1dCaches(): IntArray? {
        /*return processor.processorCaches
            .find { it.level == 1.toByte() && it.type == CentralProcessor.ProcessorCache.Type.DATA }
            ?.let { processorCache ->
                IntArray(1) { processorCache.cacheSize }
            }*/
        return null
    }

    actual fun getL1iCaches(): IntArray? {
        return null
    }

    actual fun getL2Caches(): IntArray? {
        return null
    }

    actual fun getL3Caches(): IntArray? {
        return null
    }

    actual fun getL4Caches(): IntArray? {
        return null
    }

    actual fun getNumberOfCores(): Int {
        return processor.physicalProcessorCount
    }
}