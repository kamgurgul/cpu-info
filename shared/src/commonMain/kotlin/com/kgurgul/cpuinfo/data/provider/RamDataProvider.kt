package com.kgurgul.cpuinfo.data.provider

expect class RamDataProvider() : IRamDataProvider {

    override fun getTotalBytes(): Long

    override fun getAvailableBytes(): Long

    override fun getThreshold(): Long
}
