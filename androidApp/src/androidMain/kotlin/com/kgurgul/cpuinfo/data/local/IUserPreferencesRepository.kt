package com.kgurgul.cpuinfo.data.local

import kotlinx.coroutines.flow.Flow

interface IUserPreferencesRepository {

    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun setApplicationsSortingOrder(isAscending: Boolean)

    suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean)

    suspend fun setTemperatureUnit(temperatureUnit: Int)

    suspend fun setTheme(theme: String)
}