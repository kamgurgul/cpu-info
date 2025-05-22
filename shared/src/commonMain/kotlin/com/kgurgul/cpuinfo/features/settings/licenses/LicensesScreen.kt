package com.kgurgul.cpuinfo.features.settings.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.action_not_supported
import com.kgurgul.cpuinfo.shared.back
import com.kgurgul.cpuinfo.shared.ic_open_in_browser
import com.kgurgul.cpuinfo.shared.licenses
import com.kgurgul.cpuinfo.shared.licenses_module_license
import com.kgurgul.cpuinfo.shared.licenses_module_name
import com.kgurgul.cpuinfo.shared.licenses_module_version
import com.kgurgul.cpuinfo.shared.licenses_webpage
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.FilledButton
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.safeOpenUri
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LicensesScreen(
    onNavigateBackClicked: () -> Unit,
    viewModel: LicensesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    LicensesScreen(
        uiState = uiState,
        onNavigateBackClicked = onNavigateBackClicked,
    )
}

@Composable
fun LicensesScreen(
    uiState: LicensesViewModel.UiState,
    onNavigateBackClicked: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.licenses),
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBackClicked
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(spacingMedium),
                    verticalArrangement = Arrangement.spacedBy(spacingMedium),
                ) {
                    itemsIndexed(
                        uiState.licenses,
                        key = { _, item -> item.moduleName }
                    ) { index, item ->
                        LicenseItem(
                            license = item,
                            onLicenseUrlClicked = {
                                uriHandler.safeOpenUri(it)
                                    .onFailure {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = getString(Res.string.action_not_supported),
                                            )
                                        }
                                    }
                            },
                        )
                        if (index != uiState.licenses.lastIndex) {
                            CpuDivider(
                                modifier = Modifier.padding(top = spacingSmall),
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
    }
}

@Composable
private fun LicenseItem(
    license: License,
    onLicenseUrlClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.licenses_module_name),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.moduleName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.licenses_module_version),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.moduleVersion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.licenses_module_license),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.license,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        FilledButton(
            text = stringResource(Res.string.licenses_webpage),
            onClick = { onLicenseUrlClicked(license.licenseUrl) },
            iconResource = Res.drawable.ic_open_in_browser,
            iconContentDescription = stringResource(Res.string.licenses_webpage)
        )
    }
}
