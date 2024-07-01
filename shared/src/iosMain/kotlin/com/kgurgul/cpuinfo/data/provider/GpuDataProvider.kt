package com.kgurgul.cpuinfo.data.provider

import kotlinx.cinterop.memScoped
import org.koin.core.annotation.Factory
import platform.EAGL.EAGLContext
import platform.EAGL.kEAGLRenderingAPIOpenGLES3

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

    actual fun getVulkanVersion(): String {
        return "Vulkan Version"
    }
}
