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
package com.kgurgul.cpuinfo.features.information.os

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.observable.GetOsDataInteractor
import com.kgurgul.cpuinfo.domain.observe
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class OsInfoViewModel(getOsDataInteractor: GetOsDataInteractor) : ViewModel() {

    private val expandedItemKeysFlow = MutableStateFlow<Set<String>>(emptySet())

    val uiStateFlow =
        combine(getOsDataInteractor.observe(), expandedItemKeysFlow) { items, expandedItemKeys ->
                UiState(
                    isInitializing = false,
                    items = items.toImmutableList(),
                    expandedItemKeys = expandedItemKeys.toImmutableSet(),
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun onExpandableItemClick(key: String) {
        expandedItemKeysFlow.update { if (key in it) it - key else it + key }
    }

    @Stable
    data class UiState(
        val isInitializing: Boolean = true,
        val items: ImmutableList<ItemValue> = persistentListOf(),
        val expandedItemKeys: ImmutableSet<String> = persistentSetOf(),
    )
}
