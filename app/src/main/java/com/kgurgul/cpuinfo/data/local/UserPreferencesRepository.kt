package com.kgurgul.cpuinfo.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kgurgul.cpuinfo.utils.ThemeHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun setApplicationsSortingOrder(isAscending: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORTING_APPS] = isAscending
        }
    }

    suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WITH_SYSTEM_APPS] = withSystemApps
        }
    }

    suspend fun setTemperatureUnit(temperatureUnit: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEMPERATURE_UNIT] = temperatureUnit
        }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            isApplicationsSortingAscending = preferences[PreferencesKeys.SORTING_APPS] ?: true,
            withSystemApps = preferences[PreferencesKeys.WITH_SYSTEM_APPS] ?: false,
            temperatureUnit = preferences[PreferencesKeys.TEMPERATURE_UNIT] ?: 0,
            theme = preferences[PreferencesKeys.THEME] ?: ThemeHelper.DEFAULT_MODE,
        )
    }

    private object PreferencesKeys {
        val SORTING_APPS = booleanPreferencesKey("sorting_apps")
        val WITH_SYSTEM_APPS = booleanPreferencesKey("with_system_apps")
        val TEMPERATURE_UNIT = intPreferencesKey("temperature_unit")
        val THEME = stringPreferencesKey("theme")
    }
}

data class UserPreferences(
    val isApplicationsSortingAscending: Boolean,
    val withSystemApps: Boolean,
    val temperatureUnit: Int,
    val theme: String,
)