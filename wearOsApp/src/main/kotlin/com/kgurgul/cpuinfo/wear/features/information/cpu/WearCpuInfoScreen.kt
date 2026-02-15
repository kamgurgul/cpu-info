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
@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.cpu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.cpu_current_frequency
import com.kgurgul.cpuinfo.shared.cpu_frequency_stopped
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.utils.formatHz
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearCpuInfoScreen(viewModel: CpuInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearCpuInfoScreen(uiState = uiState)
}

@Composable
fun WearCpuInfoScreen(uiState: CpuInfoViewModel.UiState) {
    val columnState =
        rememberResponsiveColumnState(
            contentPadding =
                ScalingLazyColumnDefaults.padding(
                    first = ScalingLazyColumnDefaults.ItemType.Text,
                    last = ScalingLazyColumnDefaults.ItemType.Chip,
                )
        )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.cpu),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            uiState.cpuData?.let { cpuData ->
                cpuData.frequencies.forEachIndexed { i, frequency ->
                    item(key = "__frequency_$i") { FrequencyItem(index = i, frequency = frequency) }
                }
                items(
                    cpuData.cpuItems,
                    key = { it.getKey() },
                ) { itemValue ->
                    WearCpuChip(
                        label = itemValue.getName(),
                        secondaryLabel = itemValue.getValue().replace("\n", ", "),
                    )
                }
            }
        }
    }
}

@Composable
private fun FrequencyItem(index: Int, frequency: CpuData.Frequency) {
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
    WearCpuChip {
        CpuProgressBar(
            label = currentFreq,
            progress = progress,
            minMaxValues = minFreq to maxFreq,
            textColor = MaterialTheme.colors.onBackground,
            progressColor = MaterialTheme.colors.secondaryVariant,
        )
    }
}
