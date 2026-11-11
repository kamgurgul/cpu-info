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
import com.kgurgul.cpuinfo.shared.gpu_family
import com.kgurgul.cpuinfo.shared.gpu_max_threads_per_threadgroup
import com.kgurgul.cpuinfo.shared.gpu_raytracing
import com.kgurgul.cpuinfo.shared.gpu_unified_memory
import com.kgurgul.cpuinfo.shared.metal_version
import com.kgurgul.cpuinfo.shared.vram
import com.kgurgul.cpuinfo.utils.ResourceUtils
import com.kgurgul.cpuinfo.utils.Utils
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLDeviceProtocol
import platform.Metal.MTLGPUFamilyApple1
import platform.Metal.MTLGPUFamilyApple2
import platform.Metal.MTLGPUFamilyApple3
import platform.Metal.MTLGPUFamilyApple4
import platform.Metal.MTLGPUFamilyApple5
import platform.Metal.MTLGPUFamilyApple6
import platform.Metal.MTLGPUFamilyApple7
import platform.Metal.MTLGPUFamilyApple8
import platform.Metal.MTLGPUFamilyApple9
import platform.Metal.MTLGPUFamilyMac2
import platform.Metal.MTLGPUFamilyMetal3

actual class GpuDataProvider actual constructor() : IGpuDataProvider {

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            val metalDevice = MTLCreateSystemDefaultDevice()
            if (metalDevice != null) {
                val deviceName = safeObjCString(metalDevice.name)
                if (deviceName.isNotEmpty()) {
                    add(ItemValue.Text(deviceName, ""))
                }
                val metalVersion = getMetalVersion(metalDevice)
                if (metalVersion.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.metal_version, metalVersion))
                }
                val gpuFamily = getMetalGpuFamily(metalDevice)
                if (gpuFamily.isNotEmpty()) {
                    add(ItemValue.NameResource(Res.string.gpu_family, gpuFamily))
                }
                val recommendedVram = metalDevice.recommendedMaxWorkingSetSize.toLong()
                if (recommendedVram > 0L) {
                    add(
                        ItemValue.NameResource(
                            Res.string.vram,
                            Utils.humanReadableByteCount(recommendedVram),
                        )
                    )
                }
                add(
                    ItemValue.NameValueResource(
                        Res.string.gpu_unified_memory,
                        ResourceUtils.getYesNoStringResource(metalDevice.hasUnifiedMemory),
                    )
                )
                add(
                    ItemValue.NameValueResource(
                        Res.string.gpu_raytracing,
                        ResourceUtils.getYesNoStringResource(metalDevice.supportsRaytracing),
                    )
                )
                val maxThreads =
                    metalDevice.maxThreadsPerThreadgroup.useContents {
                        "${width} x ${height} x ${depth}"
                    }
                add(
                    ItemValue.NameResource(
                        Res.string.gpu_max_threads_per_threadgroup,
                        maxThreads,
                    )
                )
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

    private fun getMetalVersion(device: MTLDeviceProtocol): String {
        val isMetal3Supported = device.supportsFamily(MTLGPUFamilyMetal3)
        return if (isMetal3Supported) "3" else "2"
    }

    private fun getMetalGpuFamily(device: MTLDeviceProtocol): String {
        return when {
            device.supportsFamily(MTLGPUFamilyApple9) -> "Apple 9"
            device.supportsFamily(MTLGPUFamilyApple8) -> "Apple 8"
            device.supportsFamily(MTLGPUFamilyApple7) -> "Apple 7"
            device.supportsFamily(MTLGPUFamilyApple6) -> "Apple 6"
            device.supportsFamily(MTLGPUFamilyApple5) -> "Apple 5"
            device.supportsFamily(MTLGPUFamilyApple4) -> "Apple 4"
            device.supportsFamily(MTLGPUFamilyApple3) -> "Apple 3"
            device.supportsFamily(MTLGPUFamilyApple2) -> "Apple 2"
            device.supportsFamily(MTLGPUFamilyApple1) -> "Apple 1"
            device.supportsFamily(MTLGPUFamilyMac2) -> "Mac 2"
            else -> ""
        }
    }

    @Suppress("USELESS_CAST")
    private fun safeObjCString(value: String): String = (value as Any?)?.toString() ?: ""
}
