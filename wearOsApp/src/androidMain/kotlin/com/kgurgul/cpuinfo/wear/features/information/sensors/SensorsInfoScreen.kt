@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.sensors

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
import com.google.android.horologist.compose.material.ResponsiveListHeader
import com.kgurgul.cpuinfo.domain.model.asString
import com.kgurgul.cpuinfo.features.information.sensors.SensorsInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.sensors
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearSensorsInfoScreen(
    viewModel: SensorsInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    WearSensorsInfoScreen(
        uiState = uiState,
    )
}

@Composable
fun WearSensorsInfoScreen(
    uiState: SensorsInfoViewModel.UiState,
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
                        text = stringResource(Res.string.sensors),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(
                uiState.sensors,
                key = { sensorData -> sensorData.id },
            ) { sensorData ->
                WearCpuChip(
                    label = sensorData.name.asString(),
                    secondaryLabel = sensorData.value,
                )
            }
        }
    }
}
