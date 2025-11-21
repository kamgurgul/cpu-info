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

package com.kgurgul.cpuinfo.wear.features.information.storage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
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
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoViewModel
import com.kgurgul.cpuinfo.features.information.storage.registerStorageMountingListener
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.storage
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearStorageInfoScreen(viewModel: StorageInfoViewModel = koinViewModel()) {
    registerStorageMountingListener(viewModel::onRefreshStorage)
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearStorageInfoScreen(uiState = uiState)
}

@Composable
fun WearStorageInfoScreen(uiState: StorageInfoViewModel.UiState) {
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
                        text = stringResource(Res.string.storage),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(uiState.storageItems, key = { it.id }) { storageItem ->
                val label = buildString {
                    val notFormattedLabel = storageItem.label.asString().trim()
                    if (notFormattedLabel.isNotEmpty()) {
                        append(notFormattedLabel)
                        append(": ")
                    }
                }
                val totalReadable = Utils.humanReadableByteCount(storageItem.storageTotal)
                val usedReadable = Utils.humanReadableByteCount(storageItem.storageUsed)
                val progress =
                    if (storageItem.storageTotal != 0L)
                        storageItem.storageUsed.toFloat() / storageItem.storageTotal.toFloat()
                    else 0f
                val usedPercent = (progress * 100.0).roundToInt()
                val storageDesc = "$label$usedReadable / $totalReadable ($usedPercent%)"
                val minMaxValues =
                    Utils.humanReadableByteCount(0) to
                        Utils.humanReadableByteCount(storageItem.storageTotal)
                WearCpuChip {
                    CpuProgressBar(
                        label = storageDesc,
                        progress = progress,
                        progressHeight = 24.dp,
                        prefixImageRes = storageItem.iconDrawable,
                        minMaxValues = minMaxValues,
                        textColor = MaterialTheme.colors.onBackground,
                        titleTextStyle = MaterialTheme.typography.caption1,
                        progressColor = MaterialTheme.colors.secondaryVariant,
                    )
                }
            }
        }
    }
}
