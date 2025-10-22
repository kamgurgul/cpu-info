package com.kgurgul.cpuinfo.data.provider

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform.cpuArchitecture
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class CpuDataProvider actual constructor() : ICpuDataProvider, KoinComponent {

    private val cpuDataNativeProvider: CpuDataNativeProvider by inject()

    @OptIn(ExperimentalNativeApi::class)
    actual override fun getAbi(): String {
        return cpuArchitecture.name
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return -1L
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return -1L to -1L
    }
}
