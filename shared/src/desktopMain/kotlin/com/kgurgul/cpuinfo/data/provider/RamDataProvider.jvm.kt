package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class RamDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual fun getTotalBytes(): Long {
        return systemInfo.hardware.memory.total
    }

    actual fun getAvailableBytes(): Long {
        return systemInfo.hardware.memory.available
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
