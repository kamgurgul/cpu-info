package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.features.applications.ApplicationsScreen
import com.kgurgul.cpuinfo.features.applications.ApplicationsViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
private fun ApplicationsScreenPreview() {
    CpuInfoTheme {
        ApplicationsScreen(
            uiState = ApplicationsViewModel.UiState(
                applications = persistentListOf(previewAppData1, previewAppData2)
            ),
            onAppClicked = {},
            onRefreshApplications = {},
            onSnackbarDismissed = {},
            onNativeLibsDialogDismissed = {},
            onNativeLibNameClicked = {},
            onAppSettingsClicked = {},
            onAppUninstallClicked = {},
            onNativeLibsClicked = {},
            onSystemAppsSwitched = {},
            onSortOrderChange = {},
        )
    }
}

private val previewAppData1 = ExtendedApplicationData(
    name = "Cpu Info",
    packageName = "com.kgurgul.cpuinfo",
    sourceDir = "/testDir",
    nativeLibs = emptyList(),
    hasNativeLibs = false,
    appIconUri = "https://avatars.githubusercontent.com/u/6407041?s=32&v=4"
)

private val previewAppData2 = ExtendedApplicationData(
    name = "Cpu Info1",
    packageName = "com.kgurgul.cpuinfo1",
    sourceDir = "/testDir",
    nativeLibs = emptyList(),
    hasNativeLibs = false,
    appIconUri = "https://avatars.githubusercontent.com/u/6407041?s=32&v=4"
)