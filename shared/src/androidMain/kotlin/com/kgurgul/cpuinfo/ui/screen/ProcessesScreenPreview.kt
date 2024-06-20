package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.features.processes.ProcessesScreen
import com.kgurgul.cpuinfo.features.processes.ProcessesViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun ProcessesScreenPreview() {
    CpuInfoTheme {
        ProcessesScreen(
            uiState = ProcessesViewModel.UiState()
        )
    }
}