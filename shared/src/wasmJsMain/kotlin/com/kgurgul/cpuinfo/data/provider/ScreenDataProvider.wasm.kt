package com.kgurgul.cpuinfo.data.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.component.KoinComponent

actual class ScreenDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return emptyList()
    }

    actual fun getOrientationFlow(): Flow<String> = emptyFlow()
}
