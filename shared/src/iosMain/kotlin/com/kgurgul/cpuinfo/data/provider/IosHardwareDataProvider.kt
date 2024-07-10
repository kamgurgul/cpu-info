package com.kgurgul.cpuinfo.data.provider

interface IosHardwareDataProvider {

    fun getAvailableMemory(): Long

    fun getTotalDiskSpaceInBytes(): Long

    fun getFreeDiskSpaceInBytes(): Long

    fun getScreenWidth(): Int

    fun getScreenHeight(): Int

    fun getScreenScale(): Float

    fun getScreenBrightness(): Float

    fun getScreenMaximumFramesPerSecond(): Int

    fun getScreenCalibratedLatency(): Double
}