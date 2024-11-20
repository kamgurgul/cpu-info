package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import kotlinx.coroutines.flow.Flow

interface IScreenDataProvider {

    suspend fun getData(): List<ItemValue>

    fun getOrientationFlow(): Flow<String>
}
