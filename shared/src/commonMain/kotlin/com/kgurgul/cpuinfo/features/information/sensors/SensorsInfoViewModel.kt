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

package com.kgurgul.cpuinfo.features.information.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.domain.observable.SensorsDataObservable
import com.kgurgul.cpuinfo.domain.observe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class SensorsInfoViewModel(
    sensorsDataObservable: SensorsDataObservable,
) : ViewModel() {

    val uiStateFlow = sensorsDataObservable.observe()
        .scan(emptyList<SensorData>()) { previous, new ->
            buildList {
                addAll(previous)
                new.forEach { newSensorData ->
                    val updatedRowId = indexOfFirst { it.id == newSensorData.id }
                    if (updatedRowId != -1) {
                        set(
                            updatedRowId,
                            SensorData(
                                id = newSensorData.id,
                                name = newSensorData.name,
                                value = newSensorData.value,
                            )
                        )
                    } else {
                        add(newSensorData)
                    }
                }
            }
        }
        .map { UiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    data class UiState(
        val sensors: List<SensorData> = emptyList()
    )
}