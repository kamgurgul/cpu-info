package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSProcessInfo

@Factory
actual class RamDataProvider actual constructor() : KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual fun getTotalBytes(): Long {
        return NSProcessInfo.processInfo().physicalMemory.toLong()
    }

    actual fun getAvailableBytes(): Long {
        return iosHardwareDataProvider.getAvailableMemory()
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
