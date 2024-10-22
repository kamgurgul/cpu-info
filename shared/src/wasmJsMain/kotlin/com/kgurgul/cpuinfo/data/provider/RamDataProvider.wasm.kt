package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class RamDataProvider actual constructor() : KoinComponent {

    actual fun getTotalBytes(): Long = getWebAssemblyMemoryBuffer()

    actual fun getAvailableBytes(): Long {
        return -1L
    }

    actual fun getThreshold(): Long {
        return -1L
    }
}

private fun getWebAssemblyMemoryBuffer(): Long = js("performance.memory.totalJSHeapSize")
