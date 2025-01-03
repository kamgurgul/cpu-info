@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.hardware

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ListHeaderDefaults.itemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.hardware.HardwareInfoViewModel
import com.kgurgul.cpuinfo.features.information.hardware.registerPowerPlugListener
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.hardware
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearHardwareInfoScreen(
    viewModel: HardwareInfoViewModel = koinViewModel(),
) {
    registerPowerPlugListener(
        onRefresh = viewModel::refreshHardwareInfo,
    )
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearHardwareInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun WearHardwareInfoScreen(
    uiState: HardwareInfoViewModel.UiState,
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
                        text = stringResource(Res.string.hardware),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(
                uiState.hardwareItems,
                key = { itemValue -> itemValue.getKey() },
            ) { itemValue ->
                if (itemValue.getValue().isEmpty()) {
                    ResponsiveListHeader(contentPadding = itemPadding()) {
                        Text(
                            text = itemValue.getName(),
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                } else {
                    WearCpuChip(
                        label = itemValue.getName(),
                        secondaryLabel = itemValue.getValue(),
                    )
                }
            }
        }
    }
}
