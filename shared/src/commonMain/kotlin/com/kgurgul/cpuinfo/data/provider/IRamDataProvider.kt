package com.kgurgul.cpuinfo.data.provider

interface IRamDataProvider {

    fun getTotalBytes(): Long

    fun getAvailableBytes(): Long

    fun getThreshold(): Long
}
