package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class GpuDataProvider() {

    suspend fun getData(): List<Pair<String, String>>
}