package com.kgurgul.cpuinfo.features.information.cpu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall

@Composable
fun CpuInfoScreen(viewModel: CpuInfoViewModel) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    CpuInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun CpuInfoScreen(uiState: CpuInfoViewModel.UiState) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
    ) {
        uiState.cpuData?.let { cpuData ->
            cpuData.frequencies.forEachIndexed { i, frequency ->
                item(key = "__frequency_$i") {
                    FrequencyItem(
                        index = i,
                        frequency = frequency,
                    )
                    if (i == uiState.cpuData.frequencies.lastIndex) {
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        CpuDivider()
                    }
                }
            }
            item(key = "__soc_name") {
                ItemValueRow(
                    title = stringResource(id = R.string.cpu_soc_name),
                    value = cpuData.processorName,
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__abi") {
                ItemValueRow(
                    title = stringResource(id = R.string.cpu_abi),
                    value = cpuData.abi,
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__cores") {
                ItemValueRow(
                    title = stringResource(id = R.string.cpu_cores),
                    value = cpuData.coreNumber.toString(),
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__has_neon") {
                ItemValueRow(
                    title = stringResource(id = R.string.cpu_has_neon),
                    value = if (cpuData.hasArmNeon) {
                        stringResource(id = R.string.yes)
                    } else {
                        stringResource(id = R.string.no)
                    },
                )

            }
            if (cpuData.l1dCaches.isNotEmpty()) {
                item(key = "__l1d") {
                    CpuDivider()
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    ItemValueRow(
                        title = stringResource(id = R.string.cpu_l1d),
                        value = cpuData.l1dCaches,
                    )
                }
            }
            if (cpuData.l1iCaches.isNotEmpty()) {
                item(key = "__l1i") {
                    CpuDivider()
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    ItemValueRow(
                        title = stringResource(id = R.string.cpu_l1i),
                        value = cpuData.l1iCaches,
                    )
                }
            }
            if (cpuData.l2Caches.isNotEmpty()) {
                item(key = "__l2") {
                    CpuDivider()
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    ItemValueRow(
                        title = stringResource(id = R.string.cpu_l2),
                        value = cpuData.l2Caches,
                    )
                }
            }
            if (cpuData.l3Caches.isNotEmpty()) {
                item(key = "__l3") {
                    CpuDivider()
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    ItemValueRow(
                        title = stringResource(id = R.string.cpu_l3),
                        value = cpuData.l3Caches,
                    )
                }
            }
            if (cpuData.l4Caches.isNotEmpty()) {
                item(key = "__l4") {
                    CpuDivider()
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    ItemValueRow(
                        title = stringResource(id = R.string.cpu_l4),
                        value = cpuData.l4Caches,
                    )
                }
            }
        }
    }
}

@Composable
fun FrequencyItem(index: Int, frequency: CpuData.Frequency) {
    val currentFreq = if (frequency.current != -1L) {
        stringResource(
            R.string.cpu_current_frequency,
            index,
            frequency.current.toString()
        )
    } else {
        stringResource(R.string.cpu_frequency_stopped, index)
    }
    val minFreq = if (frequency.min != -1L) {
        stringResource(R.string.cpu_frequency, "0")
    } else {
        ""
    }
    val maxFreq = if (frequency.max != -1L) {
        stringResource(R.string.cpu_frequency, frequency.max.toString())
    } else {
        ""
    }
    CpuProgressBar(
        label = currentFreq,
        progress = frequency.current.toFloat() / frequency.max.toFloat(),
        minMaxValues = minFreq to maxFreq,
    )
}

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