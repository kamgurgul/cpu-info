package com.kgurgul.cpuinfo.data.provider

import kotlinx.coroutines.flow.Flow

expect class ScreenDataProvider() {

    suspend fun getData(): List<Pair<String, String>>

    fun getOrientationFlow(): Flow<String>
}
