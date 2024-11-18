package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class RamDataProvider actual constructor() : IRamDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override fun getTotalBytes(): Long {
        return systemInfo.hardware.memory.total
    }

    actual override fun getAvailableBytes(): Long {
        return systemInfo.hardware.memory.available
    }

    actual override fun getThreshold(): Long {
        return -1L
    }
}
