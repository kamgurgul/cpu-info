package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class ScreenDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        return emptyList()
    }
}