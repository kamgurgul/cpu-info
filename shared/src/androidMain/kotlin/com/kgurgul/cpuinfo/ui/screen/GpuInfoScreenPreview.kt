package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoScreen
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import kotlinx.collections.immutable.persistentListOf

@Preview
@Composable
fun GpuInfoScreenPreview() {
    CpuInfoTheme {
        GpuInfoScreen(
            uiState = GpuInfoViewModel.UiState(
                gpuData = persistentListOf(
                    ItemValue.Text("vulkanVersion", "vulkanVersion"),
                    ItemValue.Text("glesVersion", "glEsVersion"),
                    ItemValue.Text("metalVersion", "metalVersion"),
                    ItemValue.Text("glVendor", "glVendor"),
                    ItemValue.Text("glRenderer", "glRenderer"),
                    ItemValue.Text("glExtensions", "glExtensions"),
                ),
            ),
        )
    }
}
