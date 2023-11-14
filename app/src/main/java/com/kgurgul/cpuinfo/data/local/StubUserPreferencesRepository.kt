package com.kgurgul.cpuinfo.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StubUserPreferencesRepository : IUserPreferencesRepository {
    override val userPreferencesFlow: Flow<UserPreferences>
        get() = flow { }

    override suspend fun setApplicationsSortingOrder(isAscending: Boolean) {
    }

    override suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean) {
    }

    override suspend fun setTemperatureUnit(temperatureUnit: Int) {
    }

    override suspend fun setTheme(theme: String) {
    }
}