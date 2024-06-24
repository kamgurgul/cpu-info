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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = combine(
        _uiStateFlow,
        userPreferencesRepository.userPreferencesFlow
    ) { uiState, userPreferences ->
        uiState.copy(
            temperatureUnit = userPreferences.temperatureUnit,
            theme = userPreferences.theme,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    private val temperatureDialogOptions = persistentListOf(
        TemperatureFormatter.CELSIUS,
        TemperatureFormatter.FAHRENHEIT,
        TemperatureFormatter.KELVIN,
    )

    private val themeDialogOptions = persistentListOf(
        DarkThemeConfig.FOLLOW_SYSTEM.prefName,
        DarkThemeConfig.LIGHT.prefName,
        DarkThemeConfig.DARK.prefName,
    )

    fun onTemperatureOptionClicked() {
        _uiStateFlow.update {
            it.copy(temperatureDialogOptions = temperatureDialogOptions)
        }
    }

    fun onTemperatureDialogDismissed() {
        _uiStateFlow.update {
            it.copy(temperatureDialogOptions = null)
        }
    }

    fun onThemeOptionClicked() {
        _uiStateFlow.update {
            it.copy(themeDialogOptions = themeDialogOptions)
        }
    }

    fun onThemeDialogDismissed() {
        _uiStateFlow.update {
            it.copy(themeDialogOptions = null)
        }
    }

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
        val temperatureDialogOptions: ImmutableList<Int>? = null,
        val themeDialogOptions: ImmutableList<String>? = null,
    )
}