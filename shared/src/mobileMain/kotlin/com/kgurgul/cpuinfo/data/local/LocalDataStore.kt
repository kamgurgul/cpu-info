package com.kgurgul.cpuinfo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.utils.CpuLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class LocalDataStore actual constructor() : IDataStore, KoinComponent {

    private val dataStore: DataStore<Preferences> by inject()

    override val preferenceFlow: Flow<UserPreferences>
        get() = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    CpuLogger.e(exception) { "Error reading preferences" }
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                mapUserPreferences(preferences)
            }

    override suspend fun setValue(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override suspend fun setValue(key: String, value: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    override suspend fun setValue(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override suspend fun setValue(key: String, value: Float) {
        dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }

    override suspend fun setValue(key: String, value: Double) {
        dataStore.edit { preferences ->
            preferences[doublePreferencesKey(key)] = value
        }
    }

    override suspend fun setValue(key: String, value: Long) {
        dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            isApplicationsSortingAscending = preferences[PreferencesKeys.SORTING_APPS] ?: true,
            isProcessesSortingAscending = preferences[PreferencesKeys.SORTING_PROCESSES] ?: true,
            withSystemApps = preferences[PreferencesKeys.WITH_SYSTEM_APPS] ?: false,
            temperatureUnit = preferences[PreferencesKeys.TEMPERATURE_UNIT] ?: 0,
            theme = preferences[PreferencesKeys.THEME] ?: DarkThemeConfig.FOLLOW_SYSTEM.prefName,
        )
    }

    private object PreferencesKeys {
        val SORTING_APPS = booleanPreferencesKey(UserPreferences.KEY_SORTING_APPS)
        val SORTING_PROCESSES = booleanPreferencesKey(UserPreferences.KEY_SORTING_PROCESSES)
        val WITH_SYSTEM_APPS = booleanPreferencesKey(UserPreferences.KEY_WITH_SYSTEM_APPS)
        val TEMPERATURE_UNIT = intPreferencesKey(UserPreferences.KEY_TEMPERATURE_UNIT)
        val THEME = stringPreferencesKey(UserPreferences.KEY_THEME)
    }
}
