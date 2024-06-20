package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoScreen
import com.kgurgul.cpuinfo.features.information.screen.ScreenInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun ScreenInfoScreenPreview() {
    CpuInfoTheme {
        ScreenInfoScreen(
            uiState = ScreenInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}