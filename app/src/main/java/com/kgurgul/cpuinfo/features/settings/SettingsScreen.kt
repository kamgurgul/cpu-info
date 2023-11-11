package com.kgurgul.cpuinfo.features.settings

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.ui.components.PrimaryTopAppBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
) {
    Scaffold(
        topBar = {
            PrimaryTopAppBar(
                title = stringResource(id = R.string.settings),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        val paddingModifier = Modifier.padding(paddingValues)
        SettingsList(
            uiState = uiState,
            modifier = paddingModifier,
        )
    }
}

@Composable
private fun SettingsList(
    uiState: SettingsViewModel.UiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        uiState.temperatureUnit
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    CpuInfoTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState()
        )
    }
}
