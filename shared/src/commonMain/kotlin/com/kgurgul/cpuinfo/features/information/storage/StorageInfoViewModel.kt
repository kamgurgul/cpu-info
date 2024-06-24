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

package com.kgurgul.cpuinfo.features.information.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.invoke
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.observable.StorageDataObservable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StorageInfoViewModel(
    private val storageDataObservable: StorageDataObservable,
) : ViewModel() {

    val uiStateFlow = storageDataObservable.observe()
        .distinctUntilChanged()
        .map { UiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        onRefreshStorage()
    }

    fun onRefreshStorage() {
        storageDataObservable.invoke()
    }

    data class UiState(
        val storageItems: List<StorageItem> = emptyList(),
    )
}