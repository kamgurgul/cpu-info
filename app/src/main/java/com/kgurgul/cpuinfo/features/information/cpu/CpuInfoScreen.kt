package com.kgurgul.cpuinfo.features.information.cpu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

@Composable
fun CpuInfoScreen(viewModel: NewCpuInfoViewModel) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

}

@Composable
fun CpuInfoScreen(uiState: NewCpuInfoViewModel.UiState) {

}

@Preview
@Composable
fun CpuInfoScreenPreview() {
    CpuInfoTheme {
        CpuInfoScreen(
            uiState = NewCpuInfoViewModel.UiState(
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