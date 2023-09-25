package com.kgurgul.cpuinfo.features.information.cpu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun CpuInfoScreen(viewModel: NewCpuInfoViewModel) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    CpuInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun CpuInfoScreen(uiState: NewCpuInfoViewModel.UiState) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall)
    ) {
        uiState.cpuData?.frequencies?.forEachIndexed { i, frequency ->
            item(key = "__frequency_$i") {
                val currentFreq = if (frequency.current != -1L) {
                    stringResource(R.string.cpu_current_frequency, i, frequency.current.toString())
                } else {
                    stringResource(R.string.cpu_frequency_stopped, i)
                }
                CpuProgressBar(
                    label = currentFreq,
                )
            }
        }
    }
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