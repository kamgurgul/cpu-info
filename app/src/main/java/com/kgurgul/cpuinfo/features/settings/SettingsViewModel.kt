package com.kgurgul.cpuinfo.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.utils.ThemeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
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
        ThemeHelper.DEFAULT_MODE,
        ThemeHelper.LIGHT_MODE,
        ThemeHelper.DARK_MODE,
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
        val theme: String = ThemeHelper.DEFAULT_MODE,
        val temperatureDialogOptions: ImmutableList<Int>? = null,
        val themeDialogOptions: ImmutableList<String>? = null,
    )
}