package com.kgurgul.cpuinfo.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .map { userPreferences ->
            UiState(
                temperatureUnit = userPreferences.temperatureUnit,
                theme = userPreferences.theme,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun setTemperatureUnit(temperatureUnit: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setTemperatureUnit(temperatureUnit)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            userPreferencesRepository.setTheme(theme)
        }
    }

    data class UiState(
        val temperatureUnit: Int = 0,
        val theme: String = DarkThemeConfig.FOLLOW_SYSTEM.prefName,
        val temperatureDialogOptions: ImmutableList<Int> = persistentListOf(
            TemperatureFormatter.CELSIUS,
            TemperatureFormatter.FAHRENHEIT,
            TemperatureFormatter.KELVIN,
        ),
        val themeDialogOptions: ImmutableList<String> = persistentListOf(
            DarkThemeConfig.FOLLOW_SYSTEM.prefName,
            DarkThemeConfig.LIGHT.prefName,
            DarkThemeConfig.DARK.prefName,
        ),
    )
}
