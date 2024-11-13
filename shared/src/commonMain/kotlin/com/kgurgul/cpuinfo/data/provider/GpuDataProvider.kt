package com.kgurgul.cpuinfo.data.provider

expect class GpuDataProvider() : IGpuDataProvider {

    override suspend fun getData(): List<Pair<String, String>>
}
