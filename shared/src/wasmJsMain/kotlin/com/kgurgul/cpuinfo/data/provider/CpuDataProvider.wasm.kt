package com.kgurgul.cpuinfo.data.provider

import kotlinx.browser.window

actual class CpuDataProvider actual constructor() {

    actual fun getAbi(): String {
        return "WASM"
    }

    actual fun getNumberOfCores(): Int {
        return window.navigator.hardwareConcurrency.toInt()
    }

    actual fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
