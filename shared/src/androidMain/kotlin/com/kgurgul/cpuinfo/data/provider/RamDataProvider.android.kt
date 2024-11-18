package com.kgurgul.cpuinfo.data.provider

import android.app.ActivityManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class RamDataProvider actual constructor() : IRamDataProvider, KoinComponent {

    private val activityManager: ActivityManager by inject()

    actual override fun getTotalBytes(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem
    }

    actual override fun getAvailableBytes(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem
    }

    actual override fun getThreshold(): Long {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.threshold
    }
}
