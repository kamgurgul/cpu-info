package com.kgurgul.cpuinfo.features.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
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
import com.kgurgul.cpuinfo.shared.ic_cpp_logo
import com.kgurgul.cpuinfo.shared.ic_settings
import com.kgurgul.cpuinfo.shared.ic_thrash
import com.kgurgul.cpuinfo.shared.native_libs
import com.kgurgul.cpuinfo.shared.ok
import com.kgurgul.cpuinfo.shared.settings
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuSnackbar
import com.kgurgul.cpuinfo.ui.components.CpuSwitchBox
import com.kgurgul.cpuinfo.ui.components.DraggableBox
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.rowActionIconSize
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ApplicationsRoute

fun NavGraphBuilder.applicationsScreen() {
    composable<ApplicationsRoute>(
        deepLinks = listOf(
            navDeepLink<ApplicationsRoute>(
                basePath = NavigationConst.BASE_URL + NavigationConst.APPLICATIONS
            )
        )
    ) {
        ApplicationsScreen()
    }
}

@Composable
fun ApplicationsScreen(
    viewModel: ApplicationsViewModel = koinViewModel(),
) {
    registerUninstallListener(
        onRefresh = viewModel::onRefreshApplications,
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    ApplicationsScreen(
        uiState = uiState,
        onAppClicked = viewModel::onApplicationClicked,
        onRefreshApplications = viewModel::onRefreshApplications,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
        onNativeLibsDialogDismissed = viewModel::onNativeLibsDialogDismissed,
        onNativeLibNameClicked = viewModel::onNativeLibsNameClicked,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onNativeLibsClicked = viewModel::onNativeLibsClicked,
        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
        onSortOrderChange = viewModel::onSortOrderChange,
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
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    onSortOrderChange: (ascending: Boolean) -> Unit,
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
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                CpuSnackbar(data)
            }
        },
    ) { innerPaddingModifier ->
        CpuPullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { onRefreshApplications() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                onAppClicked = onAppClicked,
                onAppUninstallClicked = onAppUninstallClicked,
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
) {
    var showMenu by remember { mutableStateOf(false) }
    PrimaryTopAppBar(
        title = stringResource(Res.string.applications),
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
                        CpuSwitchBox(
                            text = stringResource(Res.string.apps_show_system_apps),
                            isChecked = withSystemApps,
                            onCheckedChange = { onSystemAppsSwitched(!withSystemApps) },
                        )
                    },
                    onClick = { onSystemAppsSwitched(!withSystemApps) },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(Res.string.apps_sort_order),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = { onSortOrderChange(!isSortAscending) },
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
private fun ApplicationsList(
    appList: ImmutableList<ExtendedApplicationData>,
    onAppClicked: (packageName: String) -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        var revealedCardId: String? by rememberSaveable { mutableStateOf(null) }
        val density = LocalDensity.current
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            itemsIndexed(
                items = appList,
                key = { _, item -> item.packageName },
            ) { index, item ->
                val isRevealed by remember {
                    derivedStateOf { revealedCardId == item.packageName }
                }
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
                        )
                    },
                    modifier = Modifier.animateItem(),
                )
                if (index < appList.lastIndex) {
                    CpuDivider(
                        modifier = Modifier.padding(horizontal = spacingSmall),
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
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    onAppClicked: (packageName: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .clickable(onClick = { onAppClicked(appData.packageName) })
            .padding(spacingSmall),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(appData.appIconUri)
                .crossfade(true)
                .build(),
            contentDescription = appData.name,
            modifier = Modifier.size(50.dp),
        )
        Box(
            modifier = Modifier.weight(1f)
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier.padding(horizontal = spacingXSmall),
                ) {
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
            title = {
                Text(text = stringResource(Res.string.native_libs))
            },
            text = {
                LazyColumn {
                    items(nativeLibs) { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNativeLibNameClicked(item) }
                                .padding(vertical = spacingMedium),
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(Res.string.ok))
                }
            },
        )
    }
}

@Composable
expect fun registerUninstallListener(onRefresh: () -> Unit)
