package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class CpuDataProvider actual constructor() : KoinComponent {

    actual fun getAbi(): String {
        return "WASM"
    }

    actual fun getNumberOfCores(): Int {
        return 1
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
