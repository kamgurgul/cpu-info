package com.kgurgul.cpuinfo.features.information.ram

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.available_memory
import com.kgurgul.cpuinfo.shared.threshold
import com.kgurgul.cpuinfo.shared.total_memory
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RamInfoScreen(
    viewModel: RamInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    RamInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun RamInfoScreen(
    uiState: RamInfoViewModel.UiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            contentPadding = PaddingValues(spacingSmall),
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            uiState.ramData?.let { ramData ->
                item(key = "__total") {
                    InformationRow(
                        title = stringResource(Res.string.total_memory),
                        value = Utils.convertBytesToMega(ramData.total),
                        isLastItem = false,
                    )
                }
                item(key = "__available") {
                    InformationRow(
                        title = stringResource(Res.string.available_memory),
                        value = "${Utils.convertBytesToMega(ramData.available)} " +
                            "(${ramData.availablePercentage}%)",
                        isLastItem = false,
                    )
                }
                if (ramData.threshold != -1L) {
                    item(key = "__threshold") {
                        InformationRow(
                            title = stringResource(Res.string.threshold),
                            value = Utils.convertBytesToMega(ramData.threshold),
                            isLastItem = ramData.additionalData.isEmpty(),
                        )
                    }
                }
                ramData.additionalData.forEachIndexed { index, item ->
                    item {
                        InformationRow(
                            title = item.getName(),
                            value = item.getValue(),
                            isLastItem = index == ramData.additionalData.lastIndex,
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
