package com.kgurgul.cpuinfo.features.information.gpu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.model.getName
import com.kgurgul.cpuinfo.domain.model.getValue
import com.kgurgul.cpuinfo.features.information.base.InformationRow
import com.kgurgul.cpuinfo.ui.components.VerticalScrollbar
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GpuInfoScreen(
    viewModel: GpuInfoViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Box {
        InternalGLSurfaceView(
            onGlInfoReceived = { vendor, renderer, extensions ->
                viewModel.onGlInfoReceived(vendor, renderer, extensions)
            },
        )
        GpuInfoScreen(
            uiState = uiState,
        )
    }
}

@Composable
fun GpuInfoScreen(
    uiState: GpuInfoViewModel.UiState,
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
            itemsIndexed(
                uiState.gpuData,
            ) { index, itemValue ->
                InformationRow(
                    title = itemValue.getName(),
                    value = itemValue.getValue(),
                    isLastItem = index == uiState.gpuData.lastIndex,
                )
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
expect fun InternalGLSurfaceView(onGlInfoReceived: (String, String, String) -> Unit)

@Preview
@Composable
fun GpuInfoScreenPreview() {
    CpuInfoTheme {
        GpuInfoScreen(
            uiState = GpuInfoViewModel.UiState(
                gpuData = persistentListOf(
                    ItemValue.Text("vulkanVersion", "vulkanVersion"),
                    ItemValue.Text("glesVersion", "glEsVersion"),
                    ItemValue.Text("metalVersion", "metalVersion"),
                    ItemValue.Text("glVendor", "glVendor"),
                    ItemValue.Text("glRenderer", "glRenderer"),
                    ItemValue.Text("glExtensions", "glExtensions"),
                ),
            ),
        )
    }
}
