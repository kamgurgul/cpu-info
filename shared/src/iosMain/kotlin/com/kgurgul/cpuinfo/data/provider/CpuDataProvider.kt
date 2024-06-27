package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class CpuDataProvider actual constructor() : KoinComponent {

    private val cpuDataNativeProvider: CpuDataNativeProvider by inject()

    actual fun getAbi(): String {
        return "iOS"
    }

    actual fun getNumberOfCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return 0L
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return 0L to 0L
    }
}