package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoScreen
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
fun HardwareInfoScreenPreview() {
    CpuInfoTheme {
        HardwareInfoScreen(
            uiState = HardwareInfoViewModel.UiState(
                persistentListOf(
                    ItemValue.Text("test", ""),
                    ItemValue.Text("test", "test"),
                ),
            ),
        )
    }
}
