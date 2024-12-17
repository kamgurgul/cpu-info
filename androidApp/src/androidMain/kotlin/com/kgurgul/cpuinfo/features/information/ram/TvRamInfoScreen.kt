package com.kgurgul.cpuinfo.features.information.ram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.available_memory
import com.kgurgul.cpuinfo.shared.threshold
import com.kgurgul.cpuinfo.shared.total_memory
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.components.tv.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import com.kgurgul.cpuinfo.utils.Utils
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvRamInfoScreen(
    viewModel: RamInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    TvRamInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun TvRamInfoScreen(
    uiState: RamInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        uiState.ramData?.let { ramData ->
            item(key = "__total") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.total_memory),
                        value = Utils.convertBytesToMega(ramData.total),
                    )
                }
            }
            item(key = "__available") {
                TvListItem {
                    ItemValueRow(
                        title = stringResource(Res.string.available_memory),
                        value = "${Utils.convertBytesToMega(ramData.available)} " +
                            "(${ramData.availablePercentage}%)",
                    )
                }
            }
            if (ramData.threshold != -1L) {
                item(key = "__threshold") {
                    TvListItem {
                        ItemValueRow(
                            title = stringResource(Res.string.threshold),
                            value = Utils.convertBytesToMega(ramData.threshold),
                        )
                    }
                }
            }
        }
    }
}
