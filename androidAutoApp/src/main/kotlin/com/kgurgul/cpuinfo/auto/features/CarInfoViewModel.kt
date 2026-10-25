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
package com.kgurgul.cpuinfo.auto.features

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.auto.domain.model.CarData
import com.kgurgul.cpuinfo.auto.domain.observable.CarDataObservable
import com.kgurgul.cpuinfo.domain.invoke
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarInfoViewModel(private val carDataObservable: CarDataObservable) : ViewModel() {

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    val uiStateFlow =
        carDataObservable
            .observe(Unit)
            .map { UiState(isInitializing = false, carData = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun onGrantPermissionsClick() {
        val missingPermissions = uiStateFlow.value.carData.missingPermissions
        if (missingPermissions.isNotEmpty()) {
            viewModelScope.launch {
                eventChannel.send(Event.RequestPermissions(missingPermissions))
            }
        }
    }

    fun onPermissionsResult() {
        carDataObservable()
    }

    @Stable data class UiState(val isInitializing: Boolean = true, val carData: CarData = CarData())

    sealed interface Event {
        data class RequestPermissions(val permissions: List<String>) : Event
    }
}
