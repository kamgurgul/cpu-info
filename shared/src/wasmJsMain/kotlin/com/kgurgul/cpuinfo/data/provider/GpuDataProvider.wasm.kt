package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class GpuDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return emptyList()
    }
}
