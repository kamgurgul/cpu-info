package com.kgurgul.cpuinfo.features.information.cpu

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.CpuData
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
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.CpuProgressBar
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CpuInfoScreen(viewModel: CpuInfoViewModel = koinViewModel()) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    CpuInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun CpuInfoScreen(uiState: CpuInfoViewModel.UiState) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag(CpuInfoScreenTestTags.LAZY_COLUMN),
        ) {
            uiState.cpuData?.let { cpuData ->
                cpuData.frequencies.forEachIndexed { i, frequency ->
                    item(key = "__frequency_$i") {
                        FrequencyItem(
                            index = i,
                            frequency = frequency,
                        )
                        if (i == cpuData.frequencies.lastIndex) {
                            Spacer(modifier = Modifier.requiredSize(spacingSmall))
                            CpuDivider()
                        }
                    }
                }
                item(key = "__soc_name") {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_soc_name),
                        value = cpuData.processorName,
                        modifier = Modifier
                            .focusable()
                            .testTag(CpuInfoScreenTestTags.SOCKET_NAME),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
                item(key = "__abi") {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_abi),
                        value = cpuData.abi,
                        modifier = Modifier.focusable(),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
                item(key = "__cores") {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_cores),
                        value = cpuData.coreNumber.toString(),
                        modifier = Modifier.focusable(),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
                item(key = "__has_neon") {
                    ItemValueRow(
                        title = stringResource(Res.string.cpu_has_neon),
                        value = if (cpuData.hasArmNeon) {
                            stringResource(Res.string.yes)
                        } else {
                            stringResource(Res.string.no)
                        },
                        modifier = Modifier.focusable(),
                    )

                }
                if (cpuData.l1dCaches.isNotEmpty()) {
                    item(key = "__l1d") {
                        CpuDivider()
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l1d),
                            value = cpuData.l1dCaches,
                            modifier = Modifier.focusable(),
                        )
                    }
                }
                if (cpuData.l1iCaches.isNotEmpty()) {
                    item(key = "__l1i") {
                        CpuDivider()
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l1i),
                            value = cpuData.l1iCaches,
                            modifier = Modifier.focusable(),
                        )
                    }
                }
                if (cpuData.l2Caches.isNotEmpty()) {
                    item(key = "__l2") {
                        CpuDivider()
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l2),
                            value = cpuData.l2Caches,
                            modifier = Modifier.focusable(),
                        )
                    }
                }
                if (cpuData.l3Caches.isNotEmpty()) {
                    item(key = "__l3") {
                        CpuDivider()
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l3),
                            value = cpuData.l3Caches,
                            modifier = Modifier.focusable(),
                        )
                    }
                }
                if (cpuData.l4Caches.isNotEmpty()) {
                    item(key = "__l4") {
                        CpuDivider()
                        Spacer(modifier = Modifier.requiredSize(spacingSmall))
                        ItemValueRow(
                            title = stringResource(Res.string.cpu_l4),
                            value = cpuData.l4Caches,
                            modifier = Modifier.focusable(),
                        )
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            scrollState = listState,
        )
    }
}

@Composable
fun FrequencyItem(index: Int, frequency: CpuData.Frequency) {
    val currentFreq = if (frequency.current != -1L) {
        stringResource(
            Res.string.cpu_current_frequency,
            index,
            frequency.current.toString()
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
    val progress = if (frequency.current != -1L) {
        frequency.current.toFloat() / frequency.max.toFloat()
    } else {
        0f
    }
    CpuProgressBar(
        label = currentFreq,
        progress = progress,
        minMaxValues = minFreq to maxFreq,
        modifier = Modifier.focusable(),
    )
}

object CpuInfoScreenTestTags {
    const val LAZY_COLUMN = "cpu_info_lazy_column"
    const val SOCKET_NAME = "cpu_info_socket_name"
}
