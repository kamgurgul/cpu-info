package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.gles_version
import com.kgurgul.cpuinfo.shared.metal_version
import kotlinx.cinterop.memScoped
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLGPUFamilyMetal3

@Factory
actual class GpuDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            val metalVersion = getMetalVersion()
            if (metalVersion.isNotEmpty()) {
                add(getString(Res.string.metal_version) to metalVersion)
            }
            val glVersion = getGlEsVersion()
            if (glVersion.isNotEmpty()) {
                add(getString(Res.string.gles_version) to glVersion)
            }
        }
    }

    private fun getGlEsVersion(): String {
        return memScoped {
            val context = runCatching {
                EAGLContext(kEAGLRenderingAPIOpenGLES3)
            }.getOrNull()
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
            val isMetal3Supported = mtlCreateSystemDefaultDevice
                ?.supportsFamily(MTLGPUFamilyMetal3) ?: false
            val isMetal2Supported = mtlCreateSystemDefaultDevice != null
            when {
                isMetal3Supported -> "3"
                isMetal2Supported -> "2"
                else -> ""
            }
        }
    }
}
