package com.kgurgul.cpuinfo.data.provider

class FakeRamDataProvider : IRamDataProvider {

    override fun getTotalBytes(): Long = 1024L

    override fun getAvailableBytes(): Long = 512L

    override fun getThreshold(): Long = 256L
}
