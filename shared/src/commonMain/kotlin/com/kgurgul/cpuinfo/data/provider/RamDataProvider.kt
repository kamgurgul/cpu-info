package com.kgurgul.cpuinfo.data.provider

expect class RamDataProvider() {

    fun getTotalBytes(): Long

    fun getAvailableBytes(): Long

    fun getThreshold(): Long
}
