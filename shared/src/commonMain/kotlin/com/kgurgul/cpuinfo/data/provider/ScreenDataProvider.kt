package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import kotlinx.coroutines.flow.Flow

expect class ScreenDataProvider() : IScreenDataProvider {

    override suspend fun getData(): List<ItemValue>

    override fun getOrientationFlow(): Flow<String>
}
