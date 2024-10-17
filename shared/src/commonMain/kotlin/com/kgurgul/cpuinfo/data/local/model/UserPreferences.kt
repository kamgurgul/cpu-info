package com.kgurgul.cpuinfo.data.local.model

data class UserPreferences(
    val isApplicationsSortingAscending: Boolean,
    val isProcessesSortingAscending: Boolean,
    val withSystemApps: Boolean,
    val temperatureUnit: Int,
    val theme: String,
)
