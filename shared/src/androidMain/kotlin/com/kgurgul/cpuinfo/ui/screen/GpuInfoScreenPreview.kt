package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun GpuInfoScreenPreview() {
    CpuInfoTheme {
        GpuInfoScreen(
            uiState = GpuInfoViewModel.UiState(
                gpuData = GpuData(
                    vulkanVersion = "vulkanVersion",
                    glesVersion = "glEsVersion",
                    glVendor = "glVendor",
                    glRenderer = "glRenderer",
                    glExtensions = "glExtensions",
                )
            )
        )
    }
}