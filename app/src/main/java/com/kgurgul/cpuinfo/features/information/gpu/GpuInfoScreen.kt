package com.kgurgul.cpuinfo.features.information.gpu

import android.opengl.GLSurfaceView
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.ui.components.CpuDivider
import com.kgurgul.cpuinfo.ui.components.ItemValueRow
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme
import com.kgurgul.cpuinfo.ui.theme.spacingSmall
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
fun GpuInfoScreen(
    viewModel: GpuInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Box {
        AndroidView(
            factory = {
                GLSurfaceView(it).apply {
                    setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                    setRenderer(object : GLSurfaceView.Renderer {
                        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                            viewModel.onGlInfoReceived(
                                gl.glGetString(GL10.GL_VENDOR),
                                gl.glGetString(GL10.GL_RENDERER),
                                gl.glGetString(GL10.GL_EXTENSIONS)
                            )
                        }

                        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
                        }

                        override fun onDrawFrame(gl: GL10) {
                        }
                    }
                    )
                }
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
                    title = stringResource(id = R.string.vulkan_version),
                    value = gpuData.vulkanVersion,
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            item(key = "__gles_version") {
                ItemValueRow(
                    title = stringResource(id = R.string.gles_version),
                    value = gpuData.glesVersion,
                )
                Spacer(modifier = Modifier.requiredSize(spacingSmall))
                CpuDivider()
            }
            if (gpuData.glVendor != null) {
                item(key = "__gl_vendor") {
                    ItemValueRow(
                        title = stringResource(id = R.string.vendor),
                        value = gpuData.glVendor,
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
            if (gpuData.glRenderer != null) {
                item(key = "__gl_renderer") {
                    ItemValueRow(
                        title = stringResource(id = R.string.renderer),
                        value = gpuData.glRenderer,
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
            if (gpuData.glExtensions != null) {
                item(key = "__gl_extensions") {
                    ItemValueRow(
                        title = stringResource(id = R.string.extensions),
                        value = gpuData.glExtensions,
                    )
                    Spacer(modifier = Modifier.requiredSize(spacingSmall))
                    CpuDivider()
                }
            }
        }
    }
}

@Preview
@Composable
fun GpuInfoScreenPreview() {
    CpuInfoTheme {
        GpuInfoScreen(
            uiState = GpuInfoViewModel.UiState(
                gpuData = GpuData(
                    vulkanVersion = "vulkanVersion",
                    glesVersion = "glEsVersion",
                    glVendor = "glVendor",
                    glRenderer = "glRenderer",
                    glExtensions = "glExtensions",
                )
            )
        )
    }
}
