package com.kgurgul.cpuinfo.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoScreen
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Preview
@Composable
fun CpuInfoScreenPreview() {
    CpuInfoTheme {
        CpuInfoScreen(
            uiState = CpuInfoViewModel.UiState(
                cpuData = CpuData(
                    processorName = "processorName",
                    abi = "abi",
                    coreNumber = 1,
                    hasArmNeon = true,
                    frequencies = listOf(
                        CpuData.Frequency(
                            min = 1,
                            max = 2,
                            current = 3
                        ),
                    ),
                    l1dCaches = "l1dCaches",
                    l1iCaches = "l1iCaches",
                    l2Caches = "l2Caches",
                    l3Caches = "l3Caches",
                    l4Caches = "l4Caches",
                )
            )
        )
    }
}