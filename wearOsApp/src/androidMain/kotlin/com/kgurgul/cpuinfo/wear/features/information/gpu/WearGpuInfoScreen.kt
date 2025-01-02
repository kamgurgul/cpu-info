@file:OptIn(ExperimentalHorologistApi::class)

package com.kgurgul.cpuinfo.wear.features.information.gpu

import androidx.compose.foundation.layout.Box
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
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoViewModel
import com.kgurgul.cpuinfo.features.information.gpu.InternalGLSurfaceView
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.gpu
import com.kgurgul.cpuinfo.wear.ui.components.WearCpuChip
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WearGpuInfoScreen(
    viewModel: GpuInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Box {
        InternalGLSurfaceView(
            onGlInfoReceived = { vendor, renderer, extensions ->
                viewModel.onGlInfoReceived(vendor, renderer, extensions)
            },
        )
        WearGpuInfoScreen(
            uiState = uiState,
        )
    }
}

@Composable
fun WearGpuInfoScreen(
    uiState: GpuInfoViewModel.UiState,
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
            item(key = "__header") {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(
                        text = stringResource(Res.string.gpu),
                        color = MaterialTheme.colors.onBackground,
                    )
                }
            }
            items(
                uiState.gpuData,
                key = { itemValue -> itemValue.getKey() },
            ) { itemValue ->
                WearCpuChip(
                    label = itemValue.getName(),
                    secondaryLabel = itemValue.getValue(),
                    secondaryLabelMaxLines = 100,
                )
            }
        }
    }
}
