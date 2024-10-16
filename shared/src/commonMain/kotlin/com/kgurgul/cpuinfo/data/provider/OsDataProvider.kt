package com.kgurgul.cpuinfo.data.provider

expect class OsDataProvider() : IOsDataProvider {

    override suspend fun getData(): List<Pair<String, String>>
}
