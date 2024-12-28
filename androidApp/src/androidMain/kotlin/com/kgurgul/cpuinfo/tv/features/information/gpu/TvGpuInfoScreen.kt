package com.kgurgul.cpuinfo.tv.features.information.gpu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.getKey
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.features.information.gpu.GpuInfoViewModel
import com.kgurgul.cpuinfo.features.information.gpu.InternalGLSurfaceView
import com.kgurgul.cpuinfo.tv.ui.components.TvListItem
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TvGpuInfoScreen(
    viewModel: GpuInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    Box {
        InternalGLSurfaceView(
            onGlInfoReceived = { vendor, renderer, extensions ->
                viewModel.onGlInfoReceived(vendor, renderer, extensions)
            },
        )
        TvGpuInfoScreen(
            uiState = uiState,
        )
    }
}

@Composable
fun TvGpuInfoScreen(
    uiState: GpuInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        items(
            uiState.gpuData,
            key = { itemValue -> itemValue.getKey() },
        ) { itemValue ->
            TvListItem {
                InformationRow(
                    title = itemValue.getName(),
                    value = itemValue.getValue(),
                    isLastItem = true,
                )
            }
        }
    }
}
