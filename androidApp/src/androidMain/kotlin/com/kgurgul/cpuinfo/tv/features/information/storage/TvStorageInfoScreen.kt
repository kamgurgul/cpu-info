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
package com.kgurgul.cpuinfo.tv.features.information.storage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.features.information.storage.StorageInfoViewModel
import com.kgurgul.cpuinfo.features.information.storage.registerStorageMountingListener
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import kotlin.math.roundToInt
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvStorageInfoScreen(viewModel: StorageInfoViewModel = koinViewModel()) {
    registerStorageMountingListener(viewModel::onRefreshStorage)
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvStorageInfoScreen(uiState = uiState)
}

@Composable
fun TvStorageInfoScreen(uiState: StorageInfoViewModel.UiState) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
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
            TvListItem {
                CpuProgressBar(
                    label = storageDesc,
                    progress = progress,
                    progressHeight = 32.dp,
                    prefixImageRes = storageItem.iconDrawable,
                    minMaxValues = minMaxValues,
                )
            }
        }
    }
}
