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

package com.kgurgul.cpuinfo.features.information.os

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.result.GetOsDataInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OsInfoViewModel(
    private val getOsDataInteractor: GetOsDataInteractor,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val data = getOsDataInteractor(Unit)
            _uiStateFlow.update { it.copy(items = data) }
        }
    }

    data class UiState(
        val items: List<Pair<String, String>> = emptyList(),
    )
}
