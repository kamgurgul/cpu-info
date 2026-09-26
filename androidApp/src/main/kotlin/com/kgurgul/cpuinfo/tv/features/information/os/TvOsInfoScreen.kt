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
package com.kgurgul.cpuinfo.tv.features.information.os

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.features.information.os.OsInfoViewModel
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvOsInfoScreen(viewModel: OsInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvOsInfoScreen(uiState = uiState, onExpandableItemClick = viewModel::onExpandableItemClick)
}

@Composable
fun TvOsInfoScreen(uiState: OsInfoViewModel.UiState, onExpandableItemClick: (String) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        uiState.items.forEach { itemValue ->
            val key = itemValue.getKey()
            if (itemValue is ItemValue.Expandable) {
                val isExpanded = key in uiState.expandedItemKeys
                item {
                    val arrowRotation by
                        animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
                    TvListItem(onClick = { onExpandableItemClick(key) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(1f)) {
                                InformationRow(
                                    title = itemValue.getName(),
                                    value = itemValue.getValue(),
                                    isLastItem = true,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiaryFixed,
                                modifier = Modifier.rotate(arrowRotation),
                            )
                        }
                    }
                }
                if (isExpanded) {
                    items(itemValue.items) { childItemValue ->
                        TvListItem {
                            ItemValueRow(
                                title = childItemValue.getName(),
                                value = childItemValue.getValue().ifEmpty { null },
                                modifier = Modifier.padding(start = spacingMedium),
                            )
                        }
                    }
                }
            } else {
                item {
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
}
