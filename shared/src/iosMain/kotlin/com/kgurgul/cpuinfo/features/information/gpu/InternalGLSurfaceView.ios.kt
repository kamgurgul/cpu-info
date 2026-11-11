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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES2
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.gles2.GL_EXTENSIONS
import platform.gles2.GL_RENDERER
import platform.gles2.GL_VENDOR
import platform.gles2.glGetString

@Composable
actual fun InternalGLSurfaceView(onGlInfoReceived: (String, String, String) -> Unit) {
    LaunchedEffect(Unit) {
        val previousContext = EAGLContext.currentContext()
        val context =
            runCatching { EAGLContext(kEAGLRenderingAPIOpenGLES3) }.getOrNull()
                ?: runCatching { EAGLContext(kEAGLRenderingAPIOpenGLES2) }.getOrNull()
        if (context != null && EAGLContext.setCurrentContext(context)) {
            val vendor = glGetString(GL_VENDOR.toUInt())?.reinterpret<ByteVar>()?.toKString() ?: ""
            val renderer =
                glGetString(GL_RENDERER.toUInt())?.reinterpret<ByteVar>()?.toKString() ?: ""
            val extensions =
                glGetString(GL_EXTENSIONS.toUInt())?.reinterpret<ByteVar>()?.toKString() ?: ""
            EAGLContext.setCurrentContext(previousContext)
            onGlInfoReceived(vendor, renderer, extensions)
        }
    }
}
