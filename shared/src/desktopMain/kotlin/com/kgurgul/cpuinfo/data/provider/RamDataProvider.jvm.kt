package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class RamDataProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val memory = systemInfo.hardware.memory

    actual fun getTotalBytes(): Long {
        return memory.total
    }

    actual fun getAvailableBytes(): Long {
        return memory.available
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
