package com.kgurgul.cpuinfo.features.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Icon
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.apps_open
import com.kgurgul.cpuinfo.shared.apps_settings
import com.kgurgul.cpuinfo.shared.apps_show_system_apps
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.shared.apps_uninstall
import com.kgurgul.cpuinfo.shared.ic_apk_document_filled
import com.kgurgul.cpuinfo.shared.ic_apk_document_outlined
import com.kgurgul.cpuinfo.shared.ok
import com.kgurgul.cpuinfo.shared.refresh
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuSnackbar
import com.kgurgul.cpuinfo.ui.components.tv.TvAlertDialog
import com.kgurgul.cpuinfo.ui.components.tv.TvButton
import com.kgurgul.cpuinfo.ui.components.tv.TvIconButton
import com.kgurgul.cpuinfo.ui.components.tv.TvListItem
import com.kgurgul.cpuinfo.ui.components.tv.TvWideButton
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.ui.theme.spacingXSmall
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvApplicationsScreen(
    viewModel: ApplicationsViewModel = koinViewModel(),
) {
    registerUninstallListener(
        onRefresh = viewModel::onRefreshApplications,
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvApplicationsScreen(
        uiState = uiState,
        onAppClicked = viewModel::onApplicationClicked,
        onRefreshApplications = viewModel::onRefreshApplications,
        onSnackbarDismissed = viewModel::onSnackbarDismissed,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onSystemAppsSwitched = viewModel::onSystemAppsSwitched,
        onSortOrderChange = viewModel::onSortOrderChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvApplicationsScreen(
    uiState: ApplicationsViewModel.UiState,
    onAppClicked: (packageName: String) -> Unit,
    onRefreshApplications: () -> Unit,
    onSnackbarDismissed: () -> Unit,
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
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
    var optionsDialogId by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopBar(
                withSystemApps = uiState.withSystemApps,
                onRefreshApplications = onRefreshApplications,
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
            enabled = false,
            onRefresh = { onRefreshApplications() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingModifier),
        ) {
            ApplicationsList(
                appList = uiState.applications,
                onAppClicked = { optionsDialogId = it },
            )
        }
        OptionsDialog(
            isVisible = optionsDialogId.isNotEmpty(),
            onDismissRequest = { optionsDialogId = "" },
            appId = optionsDialogId,
            onOpenAppClicked = onAppClicked,
            onSettingsClicked = onAppSettingsClicked,
            onUninstallAppClicked = onAppUninstallClicked,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopBar(
    withSystemApps: Boolean,
    onRefreshApplications: () -> Unit,
    onSystemAppsSwitched: (enabled: Boolean) -> Unit,
    isSortAscending: Boolean,
    onSortOrderChange: (ascending: Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingMedium, Alignment.End),
        modifier = Modifier
            .fillMaxWidth()
            .focusRestorer()
            .padding(spacingMedium),
    ) {
        TvIconButton(
            onClick = onRefreshApplications
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(Res.string.refresh),
            )
        }
        TvIconButton(
            onClick = { onSortOrderChange(!isSortAscending) }
        ) {
            val icon = if (isSortAscending) {
                Icons.Default.KeyboardArrowDown
            } else {
                Icons.Default.KeyboardArrowUp
            }
            Icon(
                imageVector = icon,
                contentDescription = stringResource(Res.string.apps_sort_order),
            )
        }
        val systemAppIcon = if (withSystemApps) {
            Res.drawable.ic_apk_document_filled
        } else {
            Res.drawable.ic_apk_document_outlined
        }
        TvIconButton(
            onClick = { onSystemAppsSwitched(!withSystemApps) }
        ) {
            Icon(
                painter = painterResource(systemAppIcon),
                contentDescription = stringResource(Res.string.apps_show_system_apps),
            )
        }
    }
}

@Composable
private fun ApplicationsList(
    appList: ImmutableList<ExtendedApplicationData>,
    onAppClicked: (packageName: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacingMedium),
    ) {
        items(
            items = appList,
            key = { item -> item.packageName },
        ) { item ->
            TvListItem(
                onClick = { onAppClicked(item.packageName) },
                modifier = Modifier.animateItem()
            ) {
                Row {
                    ApplicationItem(
                        appData = item,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(appData.appIconUri)
                .crossfade(true)
                .build(),
            contentDescription = appData.name,
            modifier = Modifier.size(50.dp),
        )
        Spacer(modifier = Modifier.size(spacingMedium))
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
}

@Composable
private fun OptionsDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    appId: String,
    onOpenAppClicked: (id: String) -> Unit,
    onSettingsClicked: (id: String) -> Unit,
    onUninstallAppClicked: (id: String) -> Unit,
) {
    if (isVisible) {
        TvAlertDialog(
            onDismissRequest = onDismissRequest,
            text = {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = spacingSmall),
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                ) {
                    item(key = "__open") {
                        TvWideButton(
                            onClick = {
                                onDismissRequest()
                                onOpenAppClicked(appId)
                            },
                            title = { Text(text = stringResource(Res.string.apps_open)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                    item(key = "__settings") {
                        TvWideButton(
                            onClick = {
                                onDismissRequest()
                                onSettingsClicked(appId)
                            },
                            title = { Text(text = stringResource(Res.string.apps_settings)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                    item(key = "__uninstall") {
                        TvWideButton(
                            onClick = {
                                onDismissRequest()
                                onUninstallAppClicked(appId)
                            },
                            title = { Text(text = stringResource(Res.string.apps_uninstall)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
            },
            confirmButton = {
                TvButton(
                    onClick = onDismissRequest,
                ) {
                    androidx.tv.material3.Text(text = stringResource(Res.string.ok))
                }
            },
        )
    }
}
