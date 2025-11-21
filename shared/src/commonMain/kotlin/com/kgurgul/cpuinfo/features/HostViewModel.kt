/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    val uiStateFlow =
        userPreferencesRepository.userPreferencesFlow
            .map { userPreferences ->
                UiState(
                    isProcessSectionVisible = processesDataObservable.areProcessesSupported(),
                    isApplicationSectionVisible =
                        applicationsDataObservable.areApplicationsSupported(),
                    isLoading = false,
                    darkThemeConfig =
                        when (userPreferences.theme) {
                            DarkThemeConfig.LIGHT.prefName -> DarkThemeConfig.LIGHT
                            DarkThemeConfig.DARK.prefName -> DarkThemeConfig.DARK
                            else -> DarkThemeConfig.FOLLOW_SYSTEM
                        },
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                UiState(
                    isProcessSectionVisible = processesDataObservable.areProcessesSupported(),
                    isApplicationSectionVisible =
                        applicationsDataObservable.areApplicationsSupported(),
                ),
            )

    data class UiState(
        val isLoading: Boolean = true,
        val isProcessSectionVisible: Boolean = false,
        val isApplicationSectionVisible: Boolean = false,
        val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    )
}
