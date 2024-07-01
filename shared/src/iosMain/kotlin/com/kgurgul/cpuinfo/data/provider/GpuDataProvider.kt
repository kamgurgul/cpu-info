package com.kgurgul.cpuinfo.data.provider

import kotlinx.cinterop.memScoped
import org.koin.core.annotation.Factory
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLGPUFamilyMetal3

@Factory
actual class GpuDataProvider actual constructor() {

    actual fun getGlEsVersion(): String {
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

    actual fun getVulkanVersion() = ""

    actual fun getMetalVersion(): String {
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
