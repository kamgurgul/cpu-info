package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface IDataStore {

    val preferenceFlow: Flow<UserPreferences>

    suspend fun setValue(key: String, value: String)

    suspend fun setValue(key: String, value: Int)

    suspend fun setValue(key: String, value: Boolean)

    suspend fun setValue(key: String, value: Float)

    suspend fun setValue(key: String, value: Double)

    suspend fun setValue(key: String, value: Long)

    suspend fun setValue(key: String, value: Set<String>)
}
