package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.component.KoinComponent

actual class LocalDataStore actual constructor() : IDataStore, KoinComponent {

    override val preferenceFlow: Flow<UserPreferences>
        get() = emptyFlow()

    override suspend fun setValue(key: String, value: String) {

    }

    override suspend fun setValue(key: String, value: Int) {

    }

    override suspend fun setValue(key: String, value: Boolean) {

    }

    override suspend fun setValue(key: String, value: Float) {

    }

    override suspend fun setValue(key: String, value: Double) {

    }

    override suspend fun setValue(key: String, value: Long) {

    }

    override suspend fun setValue(key: String, value: Set<String>) {

    }
}
