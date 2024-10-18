package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class OsDataProvider actual constructor() : IOsDataProvider, KoinComponent {

    actual override suspend fun getData(): List<Pair<String, String>> {
        return emptyList()
    }
}
