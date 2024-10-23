package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class RamDataProvider actual constructor() : KoinComponent {

    actual fun getTotalBytes(): Long {
        return runCatching { getTotalJSHeapSize().toLong() }
            .getOrElse { -1L }
    }

    actual fun getAvailableBytes(): Long {
        return runCatching {
            (
                getTotalJSHeapSize() - getUsedJSHeapSize()).toLong()
        }.getOrElse { -1L }
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}

private fun getTotalJSHeapSize(): Int = js("performance.memory.totalJSHeapSize")
private fun getUsedJSHeapSize(): Int = js("performance.memory.usedJSHeapSize")
