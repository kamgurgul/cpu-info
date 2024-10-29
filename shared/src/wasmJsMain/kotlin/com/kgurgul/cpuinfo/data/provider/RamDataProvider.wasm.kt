package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.utils.getTotalHeapSize
import com.kgurgul.cpuinfo.utils.getUsedHeapSize

actual class RamDataProvider actual constructor() {

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
