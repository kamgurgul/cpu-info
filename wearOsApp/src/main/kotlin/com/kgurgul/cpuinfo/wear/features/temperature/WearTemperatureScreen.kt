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

package com.kgurgul.cpuinfo.wear.features.temperature

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ListHeaderDefaults.itemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.features.temperature.TemperatureViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.no_temp_data
import com.kgurgul.cpuinfo.shared.no_temp_data_admin_required
import com.kgurgul.cpuinfo.shared.temperature
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearTemperatureScreen(viewModel: TemperatureViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearTemperatureScreen(uiState = uiState)
}

@Composable
fun WearTemperatureScreen(uiState: TemperatureViewModel.UiState) {
    val columnState =
        rememberResponsiveColumnState(
            contentPadding =
                ScalingLazyColumnDefaults.padding(
                    first = ScalingLazyColumnDefaults.ItemType.Text,
                    last = ScalingLazyColumnDefaults.ItemType.Chip,
                )
        )
    val coroutineScope = rememberCoroutineScope()
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.temperature),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            if (uiState.temperatureItems.isEmpty()) {
                item {
                    ResponsiveListHeader(contentPadding = itemPadding()) {
                        Text(
                            text =
                                stringResource(
                                    if (uiState.isAdminRequired) {
                                        Res.string.no_temp_data_admin_required
                                    } else {
                                        Res.string.no_temp_data
                                    }
                                ),
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                }
            } else {
                items(items = uiState.temperatureItems, key = { item -> item.id }) { item ->
                    var formattedTemp by remember { mutableStateOf("") }
                    if (!item.temperature.isNaN()) {
                        LaunchedEffect(item.temperature) {
                            coroutineScope.launch {
                                formattedTemp =
                                    uiState.temperatureFormatter.format(item.temperature)
                            }
                        }
                    }
                    WearCpuChip(
                        label = item.name.asString(),
                        secondaryLabel = formattedTemp,
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier =
                                    Modifier.size(ChipDefaults.IconSize)
                                        .wrapContentSize(align = Alignment.Center),
                            )
                        },
                    )
                }
            }
        }
    }
}
