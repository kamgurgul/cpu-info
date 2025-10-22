package com.kgurgul.cpuinfo.data.provider

expect class CpuDataProvider() : ICpuDataProvider {

    override fun getAbi(): String

    override fun getNumberOfLogicalCores(): Int

    override fun getNumberOfPhysicalCores(): Int

    override fun getCurrentFreq(coreNumber: Int): Long

    override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long>
}
