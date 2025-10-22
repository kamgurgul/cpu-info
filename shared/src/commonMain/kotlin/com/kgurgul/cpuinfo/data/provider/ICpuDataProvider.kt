package com.kgurgul.cpuinfo.data.provider

interface ICpuDataProvider {

    fun getAbi(): String

    fun getNumberOfLogicalCores(): Int

    fun getNumberOfPhysicalCores(): Int

    fun getCurrentFreq(coreNumber: Int): Long

    fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long>
}
