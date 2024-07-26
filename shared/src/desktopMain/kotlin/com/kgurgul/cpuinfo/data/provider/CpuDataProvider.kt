package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class CpuDataProvider actual constructor() {

    actual fun getAbi(): String {
        return ""
    }

    actual fun getNumberOfCores(): Int {
        return -1
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}