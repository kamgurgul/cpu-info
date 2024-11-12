package com.kgurgul.cpuinfo.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HostViewModel(
    processesDataObservable: ProcessesDataObservable,
    applicationsDataObservable: ApplicationsDataObservable,
    userPreferencesRepository: IUserPreferencesRepository,
) : ViewModel() {

    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .map { userPreferences ->
            UiState(
                isProcessSectionVisible = processesDataObservable.areProcessesSupported(),
                isApplicationSectionVisible = applicationsDataObservable.areApplicationsSupported(),
                isLoading = false,
                darkThemeConfig = when (userPreferences.theme) {
                    DarkThemeConfig.LIGHT.prefName -> DarkThemeConfig.LIGHT
                    DarkThemeConfig.DARK.prefName -> DarkThemeConfig.DARK
                    else -> DarkThemeConfig.FOLLOW_SYSTEM
                },
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState(
                isProcessSectionVisible = processesDataObservable.areProcessesSupported(),
                isApplicationSectionVisible = applicationsDataObservable.areApplicationsSupported(),
            )
        )

    data class UiState(
        val isLoading: Boolean = true,
        val isProcessSectionVisible: Boolean = false,
        val isApplicationSectionVisible: Boolean = false,
        val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    )
}
