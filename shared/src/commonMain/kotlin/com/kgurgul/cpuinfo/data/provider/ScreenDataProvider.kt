package com.kgurgul.cpuinfo.data.provider

import kotlinx.coroutines.flow.Flow

expect class ScreenDataProvider() : IScreenDataProvider {

    override suspend fun getData(): List<Pair<String, String>>

    override fun getOrientationFlow(): Flow<String>
}
