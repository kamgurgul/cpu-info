@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.ram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.features.information.ram.RamInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.available_memory
import com.kgurgul.cpuinfo.shared.ram
import com.kgurgul.cpuinfo.shared.threshold
import com.kgurgul.cpuinfo.shared.total_memory
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearRamInfoScreen(
    viewModel: RamInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearRamInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun WearRamInfoScreen(
    uiState: RamInfoViewModel.UiState,
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ScalingLazyColumnDefaults.ItemType.Text,
            last = ScalingLazyColumnDefaults.ItemType.Chip,
        ),
    )
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            columnState = columnState
        ) {
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.ram),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            uiState.ramData?.let { ramData ->
                item(key = "__total") {
                    WearCpuChip(
                        label = stringResource(Res.string.total_memory),
                        secondaryLabel = Utils.convertBytesToMega(ramData.total),
                    )
                }
                item(key = "__available") {
                    WearCpuChip(
                        label = stringResource(Res.string.available_memory),
                        secondaryLabel = "${Utils.convertBytesToMega(ramData.available)} " +
                            "(${ramData.availablePercentage}%)",
                    )
                }
                if (ramData.threshold != -1L) {
                    item(key = "__threshold") {
                        WearCpuChip(
                            label = stringResource(Res.string.threshold),
                            secondaryLabel = Utils.convertBytesToMega(ramData.threshold),
                        )
                    }
                }
            }
        }
    }
}
