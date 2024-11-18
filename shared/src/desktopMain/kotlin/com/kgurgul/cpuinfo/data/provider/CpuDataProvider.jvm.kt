package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class CpuDataProvider actual constructor() : ICpuDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual override fun getAbi(): String {
        return System.getProperty("os.arch")
    }

    actual override fun getNumberOfCores(): Int {
        return processor.physicalProcessorCount
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
