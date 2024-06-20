/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.features.information.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.domain.result.GetHardwareDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HardwareInfoViewModel(
    userPreferencesRepository: IUserPreferencesRepository,
    private val getHardwareDataInteractor: GetHardwareDataInteractor,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        userPreferencesRepository.userPreferencesFlow
            .onEach { refreshHardwareInfo() }
            .launchIn(viewModelScope)
    }

    fun refreshHardwareInfo() {
        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(hardwareItems = getHardwareDataInteractor(Unit))
            }
        }
    }

    data class UiState(
        val hardwareItems: List<Pair<String, String>> = emptyList(),
    )
}