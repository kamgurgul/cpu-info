package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.utils.CpuLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class CpuDataProvider actual constructor() : ICpuDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual override fun getAbi(): String {
        return System.getProperty("os.arch")
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return processor.logicalProcessorCount
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return processor.physicalProcessorCount
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return try {
            val max = processor.maxFreq
            val cpuLoad = processor.getProcessorCpuLoad(100)[coreNumber]
            (max * cpuLoad).toLong()
        } catch (e: Exception) {
            CpuLogger.e { "Error getting current frequency for core $coreNumber: $e" }
            -1
        }
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(0, processor.maxFreq)
    }
}
