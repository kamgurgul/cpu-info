package com.kgurgul.cpuinfo.data.provider

expect class CpuDataProvider() : ICpuDataProvider {

    override fun getAbi(): String

    override fun getNumberOfCores(): Int

    override fun getCurrentFreq(coreNumber: Int): Long

    override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long>
}
