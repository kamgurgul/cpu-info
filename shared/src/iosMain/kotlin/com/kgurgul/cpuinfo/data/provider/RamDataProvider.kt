package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.cpuinfoframework.getAvailableMemory
import org.koin.core.annotation.Factory
import platform.Foundation.NSProcessInfo

@Factory
actual class RamDataProvider actual constructor() {

    actual fun getTotalBytes(): Long {
        return NSProcessInfo.processInfo().physicalMemory.toLong()
    }

    actual fun getAvailableBytes(): Long {
        return getAvailableMemory()
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
