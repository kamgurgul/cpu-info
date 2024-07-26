package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
actual class OsDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return emptyList()
    }
}