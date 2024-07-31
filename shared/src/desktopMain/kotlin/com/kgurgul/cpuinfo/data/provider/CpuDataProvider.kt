package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class CpuDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual fun getAbi(): String {
        return System.getProperty("os.arch")
    }

    actual fun getNumberOfCores(): Int {
        return processor.physicalProcessorCount
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}