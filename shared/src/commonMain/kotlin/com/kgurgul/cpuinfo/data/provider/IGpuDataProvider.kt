package com.kgurgul.cpuinfo.data.provider

interface IGpuDataProvider {

    suspend fun getData(): List<Pair<String, String>>
}
