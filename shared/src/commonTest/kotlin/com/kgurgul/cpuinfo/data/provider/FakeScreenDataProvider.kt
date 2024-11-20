package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeScreenDataProvider(
    private val data: List<ItemValue> = emptyList(),
    private val orientationFlow: Flow<String> = emptyFlow(),
) : IScreenDataProvider {

    override suspend fun getData(): List<ItemValue> {
        return data
    }

    override fun getOrientationFlow(): Flow<String> {
        return orientationFlow
    }
}
