package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import platform.Foundation.NSProcessInfo

@Factory
actual class RamDataProvider actual constructor() {

    actual fun getTotalBytes(): Long {
        return NSProcessInfo.processInfo().physicalMemory.toLong()
    }

    actual fun getAvailableBytes(): Long {
        return 0L
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
