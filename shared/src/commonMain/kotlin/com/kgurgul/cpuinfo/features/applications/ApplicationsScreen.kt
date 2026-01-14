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
package com.kgurgul.cpuinfo.features.applications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.apps_show_system_apps
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.shared.apps_uninstall
import com.kgurgul.cpuinfo.shared.apps_uninstall_confirm_message
import com.kgurgul.cpuinfo.shared.apps_uninstall_confirm_title
import com.kgurgul.cpuinfo.shared.cancel
import com.kgurgul.cpuinfo.shared.ic_cpp_logo
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_thrash
import com.kgurgul.cpuinfo.shared.menu
import com.kgurgul.cpuinfo.shared.native_libs
import com.kgurgul.cpuinfo.shared.ok
import com.kgurgul.cpuinfo.shared.refresh
import com.kgurgul.cpuinfo.shared.search
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuSearchTextField
import com.kgurgul.cpuinfo.ui.components.CpuSnackbar
import com.kgurgul.cpuinfo.ui.components.CpuSwitchBox
import com.kgurgul.cpuinfo.ui.components.DraggableBox
import com.kgurgul.cpuinfo.ui.components.FilledButton
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.rowActionIconSize
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ApplicationsBaseRoute {

    @SerialName(NavigationConst.APPLICATIONS) @Serializable data object ApplicationsRoute
}

fun NavGraphBuilder.applicationsScreen() {
    navigation<ApplicationsBaseRoute>(
        startDestination = ApplicationsBaseRoute.ApplicationsRoute,
        deepLinks =
            listOf(
                navDeepLink<ApplicationsBaseRoute>(
                    basePath = NavigationConst.BASE_URL + NavigationConst.APPLICATIONS
                )
            ),
    ) {
        composable<ApplicationsBaseRoute.ApplicationsRoute> { ApplicationsScreen() }
    }
}

@Composable
fun ApplicationsScreen(viewModel: ApplicationsViewModel = koinViewModel()) {
    registerUninstallListener(onRefresh = viewModel::onRefreshApplications)
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    ApplicationsScreen(
        uiState = uiState,
        onAppClicked = viewModel::onApplicationClicked,
        onRefreshApplications = viewModel::onRefreshApplications,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
        onNativeLibsDialogDismissed = viewModel::onNativeLibsDialogDismissed,
        onNativeLibNameClicked = viewModel::onNativeLibsNameClicked,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppUninstallWithPathClicked = viewModel::onAppUninstallWithPathClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onNativeLibsClicked = viewModel::onNativeLibsClicked,
        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
        onSortOrderChange = viewModel::onSortOrderChange,
        searchQuery = searchQuery,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
    )
}

@Composable
fun ApplicationsScreen(
    uiState: ApplicationsViewModel.UiState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    onNativeLibsDialogDismissed: () -> Unit,
    onNativeLibNameClicked: (nativeLibraryName: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppUninstallWithPathClicked: (uninstallerPath: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    onSortOrderChange: (ascending: Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarMessageString = uiState.snackbarMessage?.let { stringResource(it) }
    LaunchedEffect(snackbarMessageString) {
        scope.launch {
            if (snackbarMessageString != null) {
                val result = snackbarHostState.showSnackbar(snackbarMessageString)
                if (result == SnackbarResult.Dismissed) {
                    onSnackbarDismissed()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                withSystemApps = uiState.withSystemApps,
                onSystemAppsSwitched = onSystemAppsSwitched,
                isSortAscending = uiState.isSortAscending,
                onSortOrderChange = onSortOrderChange,
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                hasSystemAppsFiltering = uiState.hasSystemAppsFiltering,
                hasManualRefresh = uiState.hasManualRefresh,
                onRefresh = onRefreshApplications,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> CpuSnackbar(data) } },
    ) { innerPaddingModifier ->
        CpuPullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = onRefreshApplications,
            enabled = !uiState.hasManualRefresh,
            modifier = Modifier.fillMaxSize().padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                hasAppManagement = uiState.hasAppManagement,
                onAppClicked = onAppClicked,
                onAppUninstallClicked = onAppUninstallClicked,
                onAppUninstallWithPathClicked = onAppUninstallWithPathClicked,
                onAppSettingsClicked = onAppSettingsClicked,
                onNativeLibsClicked = onNativeLibsClicked,
            )
        }
        NativeLibsDialog(
            isVisible = uiState.isDialogVisible,
            nativeLibs = uiState.nativeLibs,
            onDismissRequest = onNativeLibsDialogDismissed,
            onNativeLibNameClicked = onNativeLibNameClicked,
        )
    }
}

@Composable
private fun TopBar(
    withSystemApps: Boolean,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    isSortAscending: Boolean,
    onSortOrderChange: (ascending: Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    hasSystemAppsFiltering: Boolean,
    hasManualRefresh: Boolean,
    onRefresh: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    PrimaryTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AnimatedVisibility(visible = !showSearch) {
                    Text(
                        text = stringResource(Res.string.applications),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
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
            if (hasManualRefresh) {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(Res.string.refresh),
                    )
                }
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(Res.string.menu),
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                if (hasSystemAppsFiltering) {
                    DropdownMenuItem(
                        text = {
                            CpuSwitchBox(
                                text = stringResource(Res.string.apps_show_system_apps),
                                isChecked = withSystemApps,
                                onCheckedChange = { onSystemAppsSwitched(!withSystemApps) },
                            )
                        },
                        onClick = { onSystemAppsSwitched(!withSystemApps) },
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(Res.string.apps_sort_order),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = { onSortOrderChange(!isSortAscending) },
                    trailingIcon = {
                        val icon =
                            if (isSortAscending) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Default.KeyboardArrowUp
                            }
                        Icon(imageVector = icon, contentDescription = null)
                    },
                )
            }
        },
    )
}

@Composable
private fun ApplicationsList(
    appList: ImmutableList<ExtendedApplicationData>,
    hasAppManagement: Boolean,
    onAppClicked: (packageName: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppUninstallWithPathClicked: (uninstallerPath: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
) {
    var appToUninstall: ExtendedApplicationData? by remember { mutableStateOf(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        var revealedCardId: String? by rememberSaveable { mutableStateOf(null) }
        val density = LocalDensity.current
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            itemsIndexed(items = appList, key = { _, item -> item.packageName }) { index, item ->
                val isRevealed by remember { derivedStateOf { revealedCardId == item.packageName } }
                DraggableBox(
                    isRevealed = isRevealed,
                    onExpand = { revealedCardId = item.packageName },
                    onCollapse = {
                        if (revealedCardId == item.packageName) {
                            revealedCardId = null
                        }
                    },
                    actionRowOffset = with(density) { rowActionIconSize.toPx() * 2 },
                    actionRow = {
                        Row {
                            IconButton(
                                modifier = Modifier.requiredSize(rowActionIconSize),
                                onClick = { onAppSettingsClicked(item.packageName) },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_settings),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = stringResource(Res.string.settings),
                                )
                            }
                            IconButton(
                                modifier = Modifier.requiredSize(rowActionIconSize),
                                onClick = { onAppUninstallClicked(item.packageName) },
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_thrash),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                    content = {
                        ApplicationItem(
                            appData = item,
                            onAppClicked = onAppClicked,
                            onNativeLibsClicked = onNativeLibsClicked,
                            onUninstallClicked = { appToUninstall = item },
                        )
                    },
                    enabled = hasAppManagement,
                    modifier = Modifier.animateItem(),
                )
                if (index < appList.lastIndex) {
                    CpuDivider(modifier = Modifier.padding(horizontal = spacingSmall))
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            scrollState = listState,
        )
    }
    UninstallConfirmationDialog(
        appToUninstall = appToUninstall,
        onDismiss = { appToUninstall = null },
        onConfirm = { app ->
            app.uninstallerPath?.let { onAppUninstallWithPathClicked(it) }
            appToUninstall = null
        },
    )
}

@Composable
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    onAppClicked: (packageName: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
    onUninstallClicked: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .clickable(onClick = { onAppClicked(appData.packageName) })
                .padding(spacingSmall),
    ) {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalPlatformContext.current)
                    .data(appData.appIconUri)
                    .crossfade(true)
                    .build(),
            contentDescription = appData.name,
            modifier = Modifier.size(50.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            SelectionContainer {
                Column(modifier = Modifier.padding(horizontal = spacingXSmall)) {
                    Text(
                        text = appData.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = appData.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = appData.versionName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
        if (appData.hasNativeLibs) {
            Spacer(modifier = Modifier.size(spacingXSmall))
            IconButton(
                onClick = { onNativeLibsClicked(appData.nativeLibs) },
                modifier = Modifier.requiredSize(rowActionIconSize),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_cpp_logo),
                    contentDescription = stringResource(Res.string.native_libs),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacingSmall),
                )
            }
        }
        if (appData.hasUninstaller) {
            Spacer(modifier = Modifier.size(spacingXSmall))
            IconButton(
                onClick = onUninstallClicked,
                modifier = Modifier.requiredSize(rowActionIconSize),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_thrash),
                    contentDescription = stringResource(Res.string.apps_uninstall),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacingSmall),
                )
            }
        }
    }
}

@Composable
private fun NativeLibsDialog(
    isVisible: Boolean,
    nativeLibs: ImmutableList<String>,
    onDismissRequest: () -> Unit,
    onNativeLibNameClicked: (nativeLibraryName: String) -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(Res.string.native_libs)) },
            text = {
                LazyColumn {
                    items(nativeLibs) { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier =
                                Modifier.fillMaxWidth()
                                    .clickable { onNativeLibNameClicked(item) }
                                    .padding(vertical = spacingMedium),
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismissRequest) { Text(text = stringResource(Res.string.ok)) }
            },
        )
    }
}

@Composable
private fun UninstallConfirmationDialog(
    appToUninstall: ExtendedApplicationData?,
    onDismiss: () -> Unit,
    onConfirm: (ExtendedApplicationData) -> Unit,
) {
    if (appToUninstall != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(Res.string.apps_uninstall_confirm_title)) },
            text = {
                Text(
                    text =
                        stringResource(
                            Res.string.apps_uninstall_confirm_message,
                            appToUninstall.name,
                        ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                FilledButton(
                    text = stringResource(Res.string.apps_uninstall),
                    onClick = { onConfirm(appToUninstall) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                )
            },
            dismissButton = {
                FilledButton(
                    text = stringResource(Res.string.cancel),
                    onClick = onDismiss,
                    elevation = ButtonDefaults.elevatedButtonElevation(),
                )
            },
        )
    }
}

object ApplicationsScreenTestData {
    const val SEARCH_TEST_TAG = "searchTextField"
}

@Composable expect fun registerUninstallListener(onRefresh: () -> Unit)

@Preview
@Composable
private fun ApplicationsScreenPreview() {
    CpuInfoTheme {
        ApplicationsScreen(
            uiState =
                ApplicationsViewModel.UiState(
                    applications = persistentListOf(previewAppData1, previewAppData2)
                ),
            onAppClicked = {},
            onRefreshApplications = {},
            onSnackbarDismissed = {},
            onNativeLibsDialogDismissed = {},
            onNativeLibNameClicked = {},
            onAppSettingsClicked = {},
            onAppUninstallClicked = {},
            onAppUninstallWithPathClicked = {},
            onNativeLibsClicked = {},
            onSystemAppsSwitched = {},
            onSortOrderChange = {},
            searchQuery = "",
            onSearchQueryChanged = {},
        )
    }
}

private val previewAppData1 =
    ExtendedApplicationData(
        name = "Cpu Info",
        packageName = "com.kgurgul.cpuinfo",
        versionName = "1.0.0",
        nativeLibs = emptyList(),
        hasNativeLibs = false,
        appIconUri = "https://avatars.githubusercontent.com/u/6407041?s=32&v=4",
    )

private val previewAppData2 =
    ExtendedApplicationData(
        name = "Cpu Info1 Cpu Info1 Cpu Info1 Cpu Info1",
        packageName = "com.kgurgul.cpuinfo1com.kgurgul.cpuinfo1com.kgurgul.cpuinfo1",
        versionName = "1.0.0",
        nativeLibs = emptyList(),
        hasNativeLibs = true,
        appIconUri = "https://avatars.githubusercontent.com/u/6407041?s=32&v=4",
    )
