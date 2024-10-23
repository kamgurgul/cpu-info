package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.utils.getTotalHeapSize
import com.kgurgul.cpuinfo.utils.getUsedHeapSize
import org.koin.core.component.KoinComponent

actual class RamDataProvider actual constructor() : KoinComponent {

    actual fun getTotalBytes(): Long {
        return getTotalHeapSize().toLong()
    }

    actual fun getAvailableBytes(): Long {
        return (getTotalHeapSize() - getUsedHeapSize()).toLong()
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}
