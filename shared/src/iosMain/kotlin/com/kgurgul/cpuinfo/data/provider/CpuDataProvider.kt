package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class CpuDataProvider actual constructor() {

    actual fun getAbi(): String {
        return "iOS"
    }

    actual fun getNumberOfCores(): Int {
        return 10
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return 0L
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return 0L to 0L
    }
}