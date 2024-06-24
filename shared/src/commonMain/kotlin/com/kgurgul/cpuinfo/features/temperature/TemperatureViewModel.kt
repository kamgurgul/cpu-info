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

package com.kgurgul.cpuinfo.features.temperature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable
import com.kgurgul.cpuinfo.domain.observe
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class TemperatureViewModel(
    temperatureFormatter: TemperatureFormatter,
    temperatureDataObservable: TemperatureDataObservable,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        UiState(temperatureFormatter = temperatureFormatter)
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        temperatureDataObservable.observe()
            .onEach(::handleTemperatures)
            .launchIn(viewModelScope)
    }

    private fun handleTemperatures(temperatures: List<TemperatureItem>) {
        _uiStateFlow.update {
            it.copy(
                isLoading = false,
                temperatureItems = temperatures.toPersistentList()
            )
        }
    }

    data class UiState(
        val temperatureFormatter: TemperatureFormatter,
        val isLoading: Boolean = true,
        val temperatureItems: ImmutableList<TemperatureItem> = persistentListOf()
    )
}
