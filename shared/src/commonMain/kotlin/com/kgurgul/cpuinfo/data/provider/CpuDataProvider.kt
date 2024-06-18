package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class CpuDataProvider() {

    fun getAbi(): String

    fun getNumberOfCores(): Int

    fun getCurrentFreq(coreNumber: Int): Long

    fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long>
}