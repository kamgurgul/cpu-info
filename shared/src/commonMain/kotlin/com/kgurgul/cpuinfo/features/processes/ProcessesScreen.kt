package com.kgurgul.cpuinfo.features.processes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.shared.processes
import com.kgurgul.cpuinfo.shared.search
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuSearchTextField
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ProcessesRoute

fun NavGraphBuilder.processesScreen() {
    composable<ProcessesRoute>(
        deepLinks = listOf(
            navDeepLink<ProcessesRoute>(
                basePath = NavigationConst.BASE_URL + NavigationConst.PROCESSES
            )
        )
    ) {
        ProcessesScreen()
    }
}

@Composable
fun ProcessesScreen(
    viewModel: ProcessesViewModel = koinViewModel(),
) {
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
    Scaffold(
        topBar = {
            ProcessesTopBar(
                isSortAscending = uiState.isSortAscending,
                onSortOrderChange = onSortOrderChange,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        CpuPullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { },
            enabled = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            ProcessList(
                processes = uiState.processes,
            )
        }
    }
}

@Composable
private fun ProcessesTopBar(
    isSortAscending: Boolean,
    onSortOrderChange: (ascending: Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    PrimaryTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(visible = !showSearch) {
                    Text(
                        text = stringResource(Res.string.processes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AnimatedVisibility(visible = showSearch) {
                        CpuSearchTextField(
                            searchQuery = searchQuery,
                            onSearchQueryChanged = onSearchQueryChanged,
                            onSearchClosed = { showSearch = false },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    AnimatedVisibility(visible = !showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(Res.string.search),
                            )
                        }
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(Res.string.apps_sort_order),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = {
                        onSortOrderChange(!isSortAscending)
                        showMenu = false
                    },
                    trailingIcon = {
                        val icon = if (isSortAscending) {
                            Icons.Default.KeyboardArrowDown
                        } else {
                            Icons.Default.KeyboardArrowUp
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                        )
                    },
                )
            }
        },
    )
}

@Composable
private fun ProcessList(
    processes: ImmutableList<ProcessItem>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingSmall),
            state = listState,
            modifier = modifier,
        ) {
            itemsIndexed(
                processes,
                key = { _, process -> process.name + process.pid + process.ppid },
            ) { index, process ->
                ProcessItem(
                    item = process,
                    modifier = Modifier.animateItem(),
                )
                if (index < processes.lastIndex) {
                    CpuDivider(
                        modifier = Modifier.padding(vertical = spacingSmall),
                    )
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
private fun ProcessItem(item: ProcessItem, modifier: Modifier) {
    SelectionContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacingXSmall),
            modifier = modifier,
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            DoubleTextRow(
                text1 = "PID: ${item.pid}",
                text2 = "PPID: ${item.ppid}",
            )
            DoubleTextRow(
                text1 = "NICENESS: ${item.niceness}",
                text2 = "USER: ${item.user}",
            )
            DoubleTextRow(
                text1 = "RSS: ${Utils.humanReadableByteCount(item.rss.toLong())}",
                text2 = "VSZ: ${Utils.humanReadableByteCount(item.vsize.toLong())}",
            )
        }
    }
}

@Composable
private fun DoubleTextRow(
    text1: String,
    text2: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
    ) {
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
