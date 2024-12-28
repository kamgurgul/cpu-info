package com.kgurgul.cpuinfo.tv.features.information.cpu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.features.information.cpu.CpuInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu_abi
import com.kgurgul.cpuinfo.shared.cpu_cores
import com.kgurgul.cpuinfo.shared.cpu_current_frequency
import com.kgurgul.cpuinfo.shared.cpu_frequency
import com.kgurgul.cpuinfo.shared.cpu_frequency_stopped
import com.kgurgul.cpuinfo.shared.cpu_has_neon
import com.kgurgul.cpuinfo.shared.cpu_l1d
import com.kgurgul.cpuinfo.shared.cpu_l1i
import com.kgurgul.cpuinfo.shared.cpu_l2
import com.kgurgul.cpuinfo.shared.cpu_l3
import com.kgurgul.cpuinfo.shared.cpu_l4
import com.kgurgul.cpuinfo.shared.cpu_soc_name
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.yes
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvCpuInfoScreen(viewModel: CpuInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvCpuInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun TvCpuInfoScreen(uiState: CpuInfoViewModel.UiState) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        modifier = Modifier
            .fillMaxSize()
            .testTag(TvCpuInfoScreenTestTags.LAZY_COLUMN),
    ) {
        uiState.cpuData?.let { cpuData ->
            cpuData.frequencies.forEachIndexed { i, frequency ->
                item(key = "__frequency_$i") {
                    TvListItem {
                        FrequencyItem(
                            index = i,
                            frequency = frequency,
                        )
                    }
                }
            }
            item(key = "__soc_name") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_soc_name),
                        value = cpuData.processorName,
                        modifier = Modifier
                            .testTag(TvCpuInfoScreenTestTags.SOCKET_NAME),
                    )
                }
            }
            item(key = "__abi") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_abi),
                        value = cpuData.abi,
                    )
                }
            }
            item(key = "__cores") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_cores),
                        value = cpuData.coreNumber.toString(),
                    )
                }
            }
            item(key = "__has_neon") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_has_neon),
                        value = if (cpuData.hasArmNeon) {
                            stringResource(Res.string.yes)
                        } else {
                            stringResource(Res.string.no)
                        },
                    )
                }
            }
            if (cpuData.l1dCaches.isNotEmpty()) {
                item(key = "__l1d") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l1d),
                            value = cpuData.l1dCaches,
                        )
                    }
                }
            }
            if (cpuData.l1iCaches.isNotEmpty()) {
                item(key = "__l1i") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l1i),
                            value = cpuData.l1iCaches,
                        )
                    }
                }
            }
            if (cpuData.l2Caches.isNotEmpty()) {
                item(key = "__l2") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l2),
                            value = cpuData.l2Caches,
                        )
                    }
                }
            }
            if (cpuData.l3Caches.isNotEmpty()) {
                item(key = "__l3") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l3),
                            value = cpuData.l3Caches,
                        )
                    }
                }
            }
            if (cpuData.l4Caches.isNotEmpty()) {
                item(key = "__l4") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l4),
                            value = cpuData.l4Caches,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FrequencyItem(index: Int, frequency: CpuData.Frequency) {
    val currentFreq = if (frequency.current != -1L) {
        stringResource(
            Res.string.cpu_current_frequency,
            index,
            frequency.current.toString(),
        )
    } else {
        stringResource(Res.string.cpu_frequency_stopped, index)
    }
    val minFreq = if (frequency.min != -1L) {
        stringResource(Res.string.cpu_frequency, "0")
    } else {
        ""
    }
    val maxFreq = if (frequency.max != -1L) {
        stringResource(Res.string.cpu_frequency, frequency.max.toString())
    } else {
        ""
    }
    val progress = if (frequency.current != -1L && frequency.max != 0L) {
        frequency.current.toFloat() / frequency.max.toFloat()
    } else {
        0f
    }
    CpuProgressBar(
        label = currentFreq,
        progress = progress,
        minMaxValues = minFreq to maxFreq,
    )
}

object TvCpuInfoScreenTestTags {
    const val LAZY_COLUMN = "cpu_info_lazy_column"
    const val SOCKET_NAME = "cpu_info_socket_name"
}
