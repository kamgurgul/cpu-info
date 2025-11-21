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
package com.kgurgul.cpuinfo.features.information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InfoContainerViewModel(private val ramCleanupAction: RamCleanupAction) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onPageChanged(index: Int) {
        _uiStateFlow.update {
            it.copy(
                isRamCleanupVisible =
                    ramCleanupAction.isCleanupActionAvailable() && index == RAM_POS
            )
        }
    }

    fun onClearRamClicked() {
        viewModelScope.launch { ramCleanupAction(Unit) }
    }

    data class UiState(val isRamCleanupVisible: Boolean = false)

    companion object {
        const val CPU_POS = 0
        const val GPU_POS = 1
        const val RAM_POS = 2
        const val STORAGE_POS = 3
        const val SCREEN_POS = 4
        const val ANDROID_POS = 5
        const val HARDWARE_POS = 6
        const val SENSORS_POS = 7

        const val INFO_PAGE_AMOUNT = 8
    }
}
