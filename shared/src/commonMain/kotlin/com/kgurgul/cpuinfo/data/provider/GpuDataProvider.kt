package com.kgurgul.cpuinfo.data.provider

expect class GpuDataProvider() {

    suspend fun getData(): List<Pair<String, String>>
}
