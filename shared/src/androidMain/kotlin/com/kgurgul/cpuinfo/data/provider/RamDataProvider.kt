package com.kgurgul.cpuinfo.data.provider

import android.app.ActivityManager
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class RamDataProvider actual constructor() : KoinComponent {

    private val activityManager: ActivityManager by inject()

    actual fun getTotalBytes(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    actual fun getAvailableBytes(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    actual fun getThreshold(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.threshold
    }
}
