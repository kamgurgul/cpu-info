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
package com.kgurgul.cpuinfo.tv.features.information.cpu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu_current_frequency
import com.kgurgul.cpuinfo.shared.cpu_frequency_stopped
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.formatHz
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvCpuInfoScreen(viewModel: CpuInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvCpuInfoScreen(uiState = uiState)
}

@Composable
fun TvCpuInfoScreen(uiState: CpuInfoViewModel.UiState) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize().testTag(TvCpuInfoScreenTestTags.LAZY_COLUMN),
    ) {
        uiState.cpuData?.let { cpuData ->
            cpuData.frequencies.forEachIndexed { i, frequency ->
                item(key = "__frequency_$i") {
                    TvListItem { FrequencyItem(index = i, frequency = frequency) }
                }
            }
            items(cpuData.cpuItems) { itemValue ->
                TvListItem {
                    InformationRow(
                        title = itemValue.getName(),
                        value = itemValue.getValue(),
                        isLastItem = true,
                    )
                }
            }
        }
    }
}

@Composable
fun FrequencyItem(index: Int, frequency: CpuData.Frequency) {
    val currentFreq =
        if (frequency.current != -1L) {
            stringResource(Res.string.cpu_current_frequency, index, formatHz(frequency.current))
        } else {
            stringResource(Res.string.cpu_frequency_stopped, index)
        }
    val minFreq =
        if (frequency.min != -1L) {
            formatHz(0)
        } else {
            ""
        }
    val maxFreq =
        if (frequency.max != -1L) {
            formatHz(frequency.max)
        } else {
            ""
        }
    val progress =
        if (frequency.current != -1L && frequency.max != 0L) {
            frequency.current.toFloat() / frequency.max.toFloat()
        } else {
            0f
        }
    CpuProgressBar(label = currentFreq, progress = progress, minMaxValues = minFreq to maxFreq)
}

object TvCpuInfoScreenTestTags {
    const val LAZY_COLUMN = "cpu_info_lazy_column"
    const val SOCKET_NAME = "cpu_info_socket_name"
}
