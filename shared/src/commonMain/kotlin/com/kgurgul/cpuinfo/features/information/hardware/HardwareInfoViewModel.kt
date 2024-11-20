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
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.result.GetHardwareDataInteractor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HardwareInfoViewModel(
    userPreferencesRepository: IUserPreferencesRepository,
    private val getHardwareDataInteractor: GetHardwareDataInteractor,
) : ViewModel() {

    val uiStateFlow = userPreferencesRepository.userPreferencesFlow
        .flatMapLatest {
            getHardwareDataInteractor.observe(Unit)
                .map { UiState(it.toImmutableList()) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun refreshHardwareInfo() {
        getHardwareDataInteractor()
    }

    data class UiState(
        val hardwareItems: ImmutableList<ItemValue> = persistentListOf(),
    )
}
