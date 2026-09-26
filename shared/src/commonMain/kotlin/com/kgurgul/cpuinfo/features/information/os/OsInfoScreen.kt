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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.ExpandableInformationRow
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OsInfoScreen(viewModel: OsInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    OsInfoScreen(uiState = uiState, onExpandableItemClick = viewModel::onExpandableItemClick)
}

@Composable
fun OsInfoScreen(uiState: OsInfoViewModel.UiState, onExpandableItemClick: (String) -> Unit) {
    CpuPullToRefreshBox(
        isRefreshing = uiState.isInitializing,
        onRefresh = {},
        enabled = false,
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            uiState.items.forEachIndexed { index, itemValue ->
                val key = itemValue.getKey()
                val isLastItem = index == uiState.items.lastIndex
                if (itemValue is ItemValue.Expandable) {
                    val isExpanded = key in uiState.expandedItemKeys
                    item {
                        ExpandableInformationRow(
                            title = itemValue.getName(),
                            value = itemValue.getValue(),
                            isExpanded = isExpanded,
                            isLastItem = isLastItem && !isExpanded,
                            onClick = { onExpandableItemClick(key) },
                        )
                    }
                    if (isExpanded) {
                        itemsIndexed(itemValue.items) { childIndex, childItemValue ->
                            Column {
                                ItemValueRow(
                                    title = childItemValue.getName(),
                                    value = childItemValue.getValue().ifEmpty { null },
                                    modifier = Modifier.padding(start = spacingMedium),
                                )
                                if (!isLastItem || childIndex != itemValue.items.lastIndex) {
                                    CpuDivider(modifier = Modifier.padding(top = spacingSmall))
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Column {
                            InformationRow(
                                title = itemValue.getName(),
                                value = itemValue.getValue(),
                                isLastItem = isLastItem,
                            )
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Preview
@Composable
fun OsInfoScreenPreview() {
    CpuInfoTheme {
        OsInfoScreen(
            uiState =
                OsInfoViewModel.UiState(
                    items =
                        persistentListOf(
                            ItemValue.Text("test", ""),
                            ItemValue.Text("test2", "test"),
                            ItemValue.Expandable(
                                header = ItemValue.Text("expandable", "2"),
                                items =
                                    listOf(
                                        ItemValue.Text("child1", ""),
                                        ItemValue.Text("child2", ""),
                                    ),
                            ),
                        ),
                    expandedItemKeys = persistentSetOf("expandable"),
                ),
            onExpandableItemClick = {},
        )
    }
}
