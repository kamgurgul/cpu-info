package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.utils.getTotalHeapSize
import com.kgurgul.cpuinfo.utils.getUsedHeapSize

actual class RamDataProvider actual constructor() : IRamDataProvider {

    actual override fun getTotalBytes(): Long {
        return getTotalHeapSize().toLong()
    }

    actual override fun getAvailableBytes(): Long {
        return (getTotalHeapSize() - getUsedHeapSize()).toLong()
    }

    actual override fun getThreshold(): Long {
        return -1L
    }
}
