package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapLatest

actual class LocalDataStore actual constructor() : IDataStore {

    private val refreshPrefs = MutableSharedFlow<Unit>(extraBufferCapacity = 1).also {
        it.tryEmit(Unit)
    }

    override val preferenceFlow: Flow<UserPreferences>
        get() = refreshPrefs.mapLatest {
            getUserPreferences()
        }

    override suspend fun setValue(key: String, value: String) {
        localStorage.setItem(key, value)
        refreshPrefs.tryEmit(Unit)
    }

    override suspend fun setValue(key: String, value: Int) {
        localStorage.setItem(key, value.toString())
        refreshPrefs.tryEmit(Unit)
    }

    override suspend fun setValue(key: String, value: Boolean) {
        localStorage.setItem(key, value.toString())
        refreshPrefs.tryEmit(Unit)
    }

    override suspend fun setValue(key: String, value: Float) {
        localStorage.setItem(key, value.toString())
        refreshPrefs.tryEmit(Unit)
    }

    override suspend fun setValue(key: String, value: Double) {
        localStorage.setItem(key, value.toString())
        refreshPrefs.tryEmit(Unit)
    }

    override suspend fun setValue(key: String, value: Long) {
        localStorage.setItem(key, value.toString())
        refreshPrefs.tryEmit(Unit)
    }

    private fun getUserPreferences(): UserPreferences {
        return UserPreferences(
            isApplicationsSortingAscending = localStorage.getItem(UserPreferences.KEY_SORTING_APPS)
                ?.toBooleanStrictOrNull() ?: true,
            isProcessesSortingAscending = localStorage.getItem(UserPreferences.KEY_SORTING_PROCESSES)
                ?.toBooleanStrictOrNull() ?: true,
            withSystemApps = localStorage.getItem(UserPreferences.KEY_WITH_SYSTEM_APPS)
                ?.toBooleanStrictOrNull() ?: false,
            temperatureUnit = localStorage.getItem(UserPreferences.KEY_TEMPERATURE_UNIT)
                ?.toIntOrNull() ?: 0,
            theme = localStorage.getItem(UserPreferences.KEY_THEME)
                ?: DarkThemeConfig.FOLLOW_SYSTEM.prefName,
        )
    }
}
