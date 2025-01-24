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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.back
import com.kgurgul.cpuinfo.shared.ic_open_in_browser
import com.kgurgul.cpuinfo.shared.licenses
import com.kgurgul.cpuinfo.shared.licenses_module_license
import com.kgurgul.cpuinfo.shared.licenses_module_name
import com.kgurgul.cpuinfo.shared.licenses_module_version
import com.kgurgul.cpuinfo.shared.licenses_webpage
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingMedium
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LicensesScreen(
    onNavigateBackClicked: () -> Unit,
    viewModel: LicensesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    LicensesScreen(
        uiState = uiState,
        onNavigateBackClicked = onNavigateBackClicked,
        onLicenseUrlClicked = { uriHandler.openUri(uri = it) },
    )
}

@Composable
fun LicensesScreen(
    uiState: LicensesViewModel.UiState,
    onNavigateBackClicked: () -> Unit,
    onLicenseUrlClicked: (String) -> Unit,
) {
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
                            onLicenseUrlClicked = onLicenseUrlClicked,
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
        Button(
            onClick = { onLicenseUrlClicked(license.licenseUrl) },
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_open_in_browser),
                contentDescription = stringResource(Res.string.licenses_webpage),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = stringResource(Res.string.licenses_webpage),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
