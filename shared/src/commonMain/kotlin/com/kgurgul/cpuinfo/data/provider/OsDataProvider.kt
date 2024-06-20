package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class OsDataProvider() {

    suspend fun getData(): List<Pair<String, String>>
}