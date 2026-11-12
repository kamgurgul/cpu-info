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
package com.kgurgul.cpuinfo.features.processes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.ui.components.CpuActionToolbar
import com.kgurgul.cpuinfo.ui.components.CpuActionToolbarContentPadding
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuToolbarSearchItem
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.navigation.contentPaddingWithNavigationBar
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable data object ProcessesRoute : NavKey

fun EntryProviderScope<NavKey>.processesScreen() {
    entry<ProcessesRoute> { ProcessesScreen() }
}

@Composable
fun ProcessesScreen(viewModel: ProcessesViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    ProcessesScreen(
        uiState = uiState,
        onSortOrderChange = viewModel::onSortOrderChange,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
    )
}

@Composable
fun ProcessesScreen(
    uiState: ProcessesViewModel.UiState,
    onSortOrderChange: (ascending: Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    var showSearch by rememberSaveable { mutableStateOf(searchQuery.isNotEmpty()) }
    Scaffold(
        contentWindowInsets =
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            CpuPullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = {},
                enabled = false,
                modifier = Modifier.fillMaxSize(),
            ) {
                ProcessList(processes = uiState.processes)
            }
            ProcessesToolbar(
                showSearch = showSearch,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchClicked = { showSearch = true },
                onSearchClosed = { showSearch = false },
                isSortAscending = uiState.isSortAscending,
                onSortOrderChange = onSortOrderChange,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun ProcessesToolbar(
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onSearchClosed: () -> Unit,
    isSortAscending: Boolean,
    onSortOrderChange: (ascending: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    CpuActionToolbar(modifier = modifier) {
        CpuToolbarSearchItem(
            showSearch = showSearch,
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchClicked = onSearchClicked,
            onSearchClosed = onSearchClosed,
        )
        IconButton(onClick = { onSortOrderChange(!isSortAscending) }) {
            Icon(
                imageVector =
                    if (isSortAscending) Icons.Default.KeyboardArrowDown
                    else Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.apps_sort_order),
            )
        }
    }
}

@Composable
private fun ProcessList(processes: ImmutableList<ProcessItem>, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding =
                contentPaddingWithNavigationBar(
                    spacingSmall,
                    additionalTop = CpuActionToolbarContentPadding,
                ),
            state = listState,
            modifier = modifier,
        ) {
            itemsIndexed(
                processes,
                key = { _, process -> process.name + process.pid + process.ppid },
            ) { index, process ->
                Column(modifier = Modifier.animateItem()) {
                    ProcessItem(item = process)
                    if (index < processes.lastIndex) {
                        CpuDivider(modifier = Modifier.padding(vertical = spacingSmall))
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

@Composable
private fun ProcessItem(item: ProcessItem, modifier: Modifier = Modifier) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(spacingXSmall), modifier = modifier) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            DoubleTextRow(text1 = "PID: ${item.pid}", text2 = "PPID: ${item.ppid}")
            DoubleTextRow(text1 = "NICENESS: ${item.niceness}", text2 = "USER: ${item.user}")
            DoubleTextRow(
                text1 = "RSS: ${Utils.humanReadableByteCount(item.rss.toLong())}",
                text2 = "VSZ: ${Utils.humanReadableByteCount(item.vsize.toLong())}",
            )
        }
    }
}

@Composable
private fun DoubleTextRow(text1: String, text2: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall)) {
        Text(
            text = text1,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = text2,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview
@Composable
fun ProcessesScreenPreview() {
    CpuInfoTheme {
        ProcessesScreen(
            uiState = ProcessesViewModel.UiState(),
            onSortOrderChange = {},
            searchQuery = "",
            onSearchQueryChanged = {},
        )
    }
}
