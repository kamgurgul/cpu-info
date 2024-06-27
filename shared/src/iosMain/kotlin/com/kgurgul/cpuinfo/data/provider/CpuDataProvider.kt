package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform.cpuArchitecture

@Factory
actual class CpuDataProvider actual constructor() : KoinComponent {

    private val cpuDataNativeProvider: CpuDataNativeProvider by inject()

    @OptIn(ExperimentalNativeApi::class)
    actual fun getAbi(): String {
        return cpuArchitecture.name
    }

    actual fun getNumberOfCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return -1L
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return -1L to -1L
    }
}