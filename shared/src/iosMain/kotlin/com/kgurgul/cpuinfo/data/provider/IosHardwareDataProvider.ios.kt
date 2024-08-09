package com.kgurgul.cpuinfo.data.provider

interface IosHardwareDataProvider {

    fun getAvailableMemory(): Long

    fun getTotalDiskSpaceInBytes(): Long

    fun getFreeDiskSpaceInBytes(): Long
}