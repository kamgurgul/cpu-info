package com.kgurgul.cpuinfo.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import com.kgurgul.cpuinfo.utils.ThemeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .mapLatest { userPreferences ->
            UiState(
                temperatureUnit = userPreferences.temperatureUnit,
                theme = userPreferences.theme,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

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
    )
}