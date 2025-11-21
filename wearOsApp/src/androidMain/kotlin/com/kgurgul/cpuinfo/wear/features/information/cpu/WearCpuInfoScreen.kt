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
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.cpu_abi
import com.kgurgul.cpuinfo.shared.cpu_cores
import com.kgurgul.cpuinfo.shared.cpu_current_frequency
import com.kgurgul.cpuinfo.shared.cpu_frequency_stopped
import com.kgurgul.cpuinfo.shared.cpu_has_neon
import com.kgurgul.cpuinfo.shared.cpu_l1d
import com.kgurgul.cpuinfo.shared.cpu_l1i
import com.kgurgul.cpuinfo.shared.cpu_l2
import com.kgurgul.cpuinfo.shared.cpu_l3
import com.kgurgul.cpuinfo.shared.cpu_l4
import com.kgurgul.cpuinfo.shared.cpu_soc_name
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.yes
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
                item(key = "__soc_name") {
                    WearCpuChip(
                        label = stringResource(Res.string.cpu_soc_name),
                        secondaryLabel = cpuData.processorName,
                    )
                }
                item(key = "__abi") {
                    WearCpuChip(
                        label = stringResource(Res.string.cpu_abi),
                        secondaryLabel = cpuData.abi,
                    )
                }
                item(key = "__cores") {
                    WearCpuChip(
                        label = stringResource(Res.string.cpu_cores),
                        secondaryLabel = cpuData.coreNumber.toString(),
                    )
                }
                item(key = "__has_neon") {
                    WearCpuChip(
                        label = stringResource(Res.string.cpu_has_neon),
                        secondaryLabel =
                            if (cpuData.hasArmNeon) {
                                stringResource(Res.string.yes)
                            } else {
                                stringResource(Res.string.no)
                            },
                    )
                }
                if (cpuData.l1dCaches.isNotEmpty()) {
                    item(key = "__l1d") {
                        WearCpuChip(
                            label = stringResource(Res.string.cpu_l1d),
                            secondaryLabel = cpuData.l1dCaches.replace("\n", ", "),
                        )
                    }
                }
                if (cpuData.l1iCaches.isNotEmpty()) {
                    item(key = "__l1i") {
                        WearCpuChip(
                            label = stringResource(Res.string.cpu_l1i),
                            secondaryLabel = cpuData.l1iCaches.replace("\n", ", "),
                        )
                    }
                }
                if (cpuData.l2Caches.isNotEmpty()) {
                    item(key = "__l2") {
                        WearCpuChip(
                            label = stringResource(Res.string.cpu_l2),
                            secondaryLabel = cpuData.l2Caches.replace("\n", ", "),
                        )
                    }
                }
                if (cpuData.l3Caches.isNotEmpty()) {
                    item(key = "__l3") {
                        WearCpuChip(
                            label = stringResource(Res.string.cpu_l3),
                            secondaryLabel = cpuData.l3Caches.replace("\n", ", "),
                        )
                    }
                }
                if (cpuData.l4Caches.isNotEmpty()) {
                    item(key = "__l4") {
                        WearCpuChip(
                            label = stringResource(Res.string.cpu_l4),
                            secondaryLabel = cpuData.l4Caches.replace("\n", ", "),
                        )
                    }
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
