package com.kgurgul.cpuinfo.data.provider

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeScreenDataProvider(
    private val data: List<Pair<String, String>> = emptyList(),
    private val orientationFlow: Flow<String> = emptyFlow(),
) : IScreenDataProvider {
    override suspend fun getData(): List<Pair<String, String>> {
        return data
    }

    override fun getOrientationFlow(): Flow<String> {
        return orientationFlow
    }
}
