package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.features.settings.SettingsScreen
import com.kgurgul.cpuinfo.features.settings.SettingsViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun SettingsScreenPreview() {
    CpuInfoTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(),
            onThemeItemClicked = {},
            onTemperatureItemClicked = {},
            onTemperatureDialogDismissRequest = {},
            onTemperatureOptionClicked = {},
            onThemeDialogDismissRequest = {},
            onThemeOptionClicked = {},
        )
    }
}