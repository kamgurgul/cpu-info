package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class StubUserPreferencesRepository : IUserPreferencesRepository {

    var preferencesFlow: Flow<UserPreferences> = emptyFlow()

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = preferencesFlow

    override suspend fun setApplicationsSortingOrder(isAscending: Boolean) {
    }

    override suspend fun setProcessesSortingOrder(isAscending: Boolean) {
    }

    override suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean) {
    }

    override suspend fun setTemperatureUnit(temperatureUnit: Int) {
    }

    override suspend fun setTheme(theme: String) {
    }
}
