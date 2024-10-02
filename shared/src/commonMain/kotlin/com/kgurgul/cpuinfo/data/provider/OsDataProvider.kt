package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class OsDataProvider() : IOsDataProvider {

    override suspend fun getData(): List<Pair<String, String>>
}
