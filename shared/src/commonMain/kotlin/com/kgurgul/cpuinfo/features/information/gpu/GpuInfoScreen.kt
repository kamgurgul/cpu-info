package com.kgurgul.cpuinfo.features.information.gpu

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.extensions
import com.kgurgul.cpuinfo.shared.gles_version
import com.kgurgul.cpuinfo.shared.renderer
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.shared.vulkan_version
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import org.jetbrains.compose.resources.stringResource
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
            }
        )
        GpuInfoScreen(
            uiState = uiState
        )
    }
}

@Composable
fun GpuInfoScreen(
    uiState: GpuInfoViewModel.UiState,
) {
    LazyColumn(
        contentPadding = PaddingValues(spacingSmall),
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        modifier = Modifier.fillMaxSize(),
    ) {
        uiState.gpuData?.let { gpuData ->
            item(key = "__vulkan_version") {
                ItemValueRow(
                    title = stringResource(Res.string.vulkan_version),
                    value = gpuData.vulkanVersion,
                    modifier = Modifier.focusable(),
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__gles_version") {
                ItemValueRow(
                    title = stringResource(Res.string.gles_version),
                    value = gpuData.glesVersion,
                    modifier = Modifier.focusable(),
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            if (gpuData.glVendor != null) {
                item(key = "__gl_vendor") {
                    ItemValueRow(
                        title = stringResource(Res.string.vendor),
                        value = gpuData.glVendor,
                        modifier = Modifier.focusable(),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
            if (gpuData.glRenderer != null) {
                item(key = "__gl_renderer") {
                    ItemValueRow(
                        title = stringResource(Res.string.renderer),
                        value = gpuData.glRenderer,
                        modifier = Modifier.focusable(),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
            if (gpuData.glExtensions != null) {
                item(key = "__gl_extensions") {
                    ItemValueRow(
                        title = stringResource(Res.string.extensions),
                        value = gpuData.glExtensions,
                        modifier = Modifier.focusable(),
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
        }
    }
}

@Composable
expect fun InternalGLSurfaceView(onGlInfoReceived: (String, String, String) -> Unit)
