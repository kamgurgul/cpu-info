package com.kgurgul.cpuinfo.data.provider

import kotlinx.browser.window

actual class CpuDataProvider actual constructor() : ICpuDataProvider {

    actual override fun getAbi(): String {
        return "WASM"
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return window.navigator.hardwareConcurrency.toInt()
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return window.navigator.hardwareConcurrency.toInt()
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
