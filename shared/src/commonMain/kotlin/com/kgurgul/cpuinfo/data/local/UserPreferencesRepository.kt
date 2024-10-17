package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepository(
    private val dataStore: IDataStore,
) : IUserPreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = dataStore.preferenceFlow

    override suspend fun setApplicationsSortingOrder(isAscending: Boolean) {
        dataStore.setValue(UserPreferences.KEY_SORTING_APPS, isAscending)
    }

    override suspend fun setProcessesSortingOrder(isAscending: Boolean) {
        dataStore.setValue(UserPreferences.KEY_SORTING_PROCESSES, isAscending)
    }

    override suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean) {
        dataStore.setValue(UserPreferences.KEY_WITH_SYSTEM_APPS, withSystemApps)
    }

    override suspend fun setTemperatureUnit(temperatureUnit: Int) {
        dataStore.setValue(UserPreferences.KEY_TEMPERATURE_UNIT, temperatureUnit)
    }

    override suspend fun setTheme(theme: String) {
        dataStore.setValue(UserPreferences.KEY_THEME, theme)
    }
}
