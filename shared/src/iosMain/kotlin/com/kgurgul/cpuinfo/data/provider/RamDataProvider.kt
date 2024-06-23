package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class RamDataProvider actual constructor() {

    actual fun getTotalBytes(): Long {
        return 0L
    }

    actual fun getAvailableBytes(): Long {
        return 0L
    }

    actual fun getAvailablePercentage(): Int {
        return 0
    }

    actual fun getThreshold(): Long {
        return 0L
    }
}
