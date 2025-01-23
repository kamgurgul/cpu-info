package com.kgurgul.cpuinfo.features.settings.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.licenses
import com.kgurgul.cpuinfo.shared.licenses_module_license
import com.kgurgul.cpuinfo.shared.licenses_module_name
import com.kgurgul.cpuinfo.shared.licenses_module_version
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuPullToRefreshBox
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LicensesScreen(viewModel: LicensesViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    LicensesScreen(
        uiState = uiState,
        onLicenseUrlClicked = viewModel::onLicenseUrlClicked,
    )
}

@Composable
fun LicensesScreen(
    uiState: LicensesViewModel.UiState,
    onLicenseUrlClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(Res.string.licenses),
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
            LazyColumn(
                contentPadding = PaddingValues(spacingSmall),
                verticalArrangement = Arrangement.spacedBy(spacingSmall),
            ) {
                itemsIndexed(
                    uiState.licenses,
                    key = { _, item -> item.moduleName }
                ) { index, item ->
                    LicenseItem(
                        license = item,
                        onLicenseUrlClicked = onLicenseUrlClicked,
                    )
                    if (index == uiState.licenses.lastIndex) {
                        CpuDivider(
                            modifier = Modifier.padding(top = spacingSmall),
                        )
                    }
                }
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
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.moduleName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.licenses_module_version),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.moduleVersion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.licenses_module_license),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = license.license,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
