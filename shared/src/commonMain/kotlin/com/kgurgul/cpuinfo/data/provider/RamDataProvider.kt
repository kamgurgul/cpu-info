package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class RamDataProvider() {

    fun getTotalBytes(): Long

    fun getAvailableBytes(): Long

    fun getThreshold(): Long
}