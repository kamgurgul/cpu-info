package com.kgurgul.cpuinfo.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HostViewModel(
    processesDataObservable: ProcessesDataObservable,
    userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        UiState(
            isProcessSectionVisible = processesDataObservable.areProcessesSupported()
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        userPreferencesRepository.userPreferencesFlow
            .onEach { userPreferences ->
                _uiStateFlow.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        darkThemeConfig = when (userPreferences.theme) {
                            DarkThemeConfig.LIGHT.prefName -> DarkThemeConfig.LIGHT
                            DarkThemeConfig.DARK.prefName -> DarkThemeConfig.DARK
                            else -> DarkThemeConfig.FOLLOW_SYSTEM
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    data class UiState(
        val isLoading: Boolean = true,
        val isProcessSectionVisible: Boolean = false,
        val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    )
}