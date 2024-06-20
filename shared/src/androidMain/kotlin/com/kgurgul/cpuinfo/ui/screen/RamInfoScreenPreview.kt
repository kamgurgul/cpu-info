package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.features.information.ram.RamInfoScreen
import com.kgurgul.cpuinfo.features.information.ram.RamInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun RamInfoScreenPreview() {
    CpuInfoTheme {
        RamInfoScreen(
            uiState = RamInfoViewModel.UiState(
                ramData = RamData(
                    total = 100,
                    available = 50,
                    availablePercentage = 50,
                    threshold = 50,
                ),
            ),
        )
    }
}