package com.kgurgul.cpuinfo.features.information.gpu

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
actual fun InternalGLSurfaceView(onGlInfoReceived: (String, String, String) -> Unit) {
    AndroidView(
        factory = {
            GLSurfaceView(it).apply {
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                setRenderer(
                    object : GLSurfaceView.Renderer {
                        override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                            onGlInfoReceived(
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
        },
        modifier = Modifier.alpha(0f)
    )
}