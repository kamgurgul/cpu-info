package com.kgurgul.cpuinfo.features.applications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.apps_show_system_apps
import com.kgurgul.cpuinfo.shared.apps_sort_order
import com.kgurgul.cpuinfo.shared.ic_apk_document_filled
import com.kgurgul.cpuinfo.shared.ic_apk_document_outlined
import com.kgurgul.cpuinfo.shared.native_libs
import com.kgurgul.cpuinfo.shared.ok
import com.kgurgul.cpuinfo.shared.refresh
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.CpuSnackbar
import com.kgurgul.cpuinfo.ui.components.tv.TvIconButton
import com.kgurgul.cpuinfo.ui.components.tv.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
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
        onNativeLibsDialogDismissed = viewModel::onNativeLibsDialogDismissed,
        onNativeLibNameClicked = viewModel::onNativeLibsNameClicked,
        onAppUninstallClicked = viewModel::onAppUninstallClicked,
        onAppSettingsClicked = viewModel::onAppSettingsClicked,
        onNativeLibsClicked = viewModel::onNativeLibsClicked,
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
    onAppUninstallClicked: (id: String) -> Unit,
    onAppSettingsClicked: (id: String) -> Unit,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
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
                        onNativeLibsClicked = onNativeLibsClicked,
                        modifier = Modifier.weight(1f),
                    )
                    /*IconButton(
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
                    }*/
                }
            }
        }
    }
}

@Composable
private fun ApplicationItem(
    appData: ExtendedApplicationData,
    onNativeLibsClicked: (libs: List<String>) -> Unit,
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
        /*if (appData.hasNativeLibs) {
            Spacer(modifier = Modifier.weight(1f))
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
        }*/
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
