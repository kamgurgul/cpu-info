package com.kgurgul.cpuinfo.data.local.model

data class UserPreferences(
    val isApplicationsSortingAscending: Boolean,
    val isProcessesSortingAscending: Boolean,
    val withSystemApps: Boolean,
    val temperatureUnit: Int,
    val theme: String,
) {

    companion object {
        const val KEY_SORTING_APPS = "sorting_apps"
        const val KEY_SORTING_PROCESSES = "sorting_processes"
        const val KEY_WITH_SYSTEM_APPS = "with_system_apps"
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        const val KEY_THEME = "theme"
    }
}
