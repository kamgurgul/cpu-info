package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSProcessInfo

actual class RamDataProvider actual constructor() : IRamDataProvider, KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual override fun getTotalBytes(): Long {
        return NSProcessInfo.processInfo().physicalMemory.toLong()
    }

    actual override fun getAvailableBytes(): Long {
        return iosHardwareDataProvider.getAvailableMemory()
    }

    actual override fun getThreshold(): Long {
        return -1L
    }
}
