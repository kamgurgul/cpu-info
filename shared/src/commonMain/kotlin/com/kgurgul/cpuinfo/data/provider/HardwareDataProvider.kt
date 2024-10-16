package com.kgurgul.cpuinfo.data.provider

expect class HardwareDataProvider() {

    suspend fun getData(): List<Pair<String, String>>
}
