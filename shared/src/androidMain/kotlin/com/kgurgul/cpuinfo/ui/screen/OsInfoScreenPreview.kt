package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.features.information.os.OsInfoScreen
import com.kgurgul.cpuinfo.features.information.os.OsInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun OsInfoScreenPreview() {
    CpuInfoTheme {
        OsInfoScreen(
            uiState = OsInfoViewModel.UiState(
                listOf(
                    "test" to "",
                    "test" to "test",
                )
            ),
        )
    }
}