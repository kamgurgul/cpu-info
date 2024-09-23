package com.kgurgul.cpuinfo.data.provider

interface IOsDataProvider {

    suspend fun getData(): List<Pair<String, String>>
}