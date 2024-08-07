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
import com.kgurgul.cpuinfo.domain.invoke
import com.kgurgul.cpuinfo.domain.result.GetHardwareDataInteractor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HardwareInfoViewModel(
    userPreferencesRepository: IUserPreferencesRepository,
    private val getHardwareDataInteractor: GetHardwareDataInteractor,
) : ViewModel() {

    val uiStateFlow = getHardwareDataInteractor.observe()
        .map { UiState(it) }
        .onStart { refreshHardwareInfo() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UiState())

    init {
        userPreferencesRepository.userPreferencesFlow
            .onEach { refreshHardwareInfo() }
            .launchIn(viewModelScope)
    }

    fun refreshHardwareInfo() {
        getHardwareDataInteractor.invoke()
    }

    data class UiState(
        val hardwareItems: List<Pair<String, String>> = emptyList(),
    )
}