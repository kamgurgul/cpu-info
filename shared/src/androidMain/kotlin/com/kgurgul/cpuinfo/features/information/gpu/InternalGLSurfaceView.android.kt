/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
                                gl.glGetString(GL10.GL_EXTENSIONS),
                            )
                        }

                        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {}

                        override fun onDrawFrame(gl: GL10) {}
                    }
                )
            }
        },
        modifier = Modifier.alpha(0f),
    )
}
