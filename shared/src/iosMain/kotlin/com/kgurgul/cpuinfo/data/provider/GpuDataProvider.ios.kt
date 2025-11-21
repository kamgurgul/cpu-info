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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.gles_version
import com.kgurgul.cpuinfo.shared.metal_version
import kotlinx.cinterop.memScoped
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLGPUFamilyMetal3

actual class GpuDataProvider actual constructor() : IGpuDataProvider {

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            val metalVersion = getMetalVersion()
            if (metalVersion.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.metal_version, metalVersion))
            }
            val glVersion = getGlEsVersion()
            if (glVersion.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.gles_version, glVersion))
            }
        }
    }

    private fun getGlEsVersion(): String {
        return memScoped {
            val context = runCatching { EAGLContext(kEAGLRenderingAPIOpenGLES3) }.getOrNull()
            if (context != null) {
                "3.0"
            } else {
                "2.0"
            }
        }
    }

    private fun getMetalVersion(): String {
        return memScoped {
            val mtlCreateSystemDefaultDevice = MTLCreateSystemDefaultDevice()
            val isMetal3Supported =
                mtlCreateSystemDefaultDevice?.supportsFamily(MTLGPUFamilyMetal3) ?: false
            val isMetal2Supported = mtlCreateSystemDefaultDevice != null
            when {
                isMetal3Supported -> "3"
                isMetal2Supported -> "2"
                else -> ""
            }
        }
    }
}
