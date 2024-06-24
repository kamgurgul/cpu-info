package com.kgurgul.cpuinfo.data.provider

import kotlinx.cinterop.ExperimentalForeignApi
import libcpuinfo.cpuinfo_initialize
import org.koin.core.annotation.Single

@OptIn(ExperimentalForeignApi::class)
@Single(createdAtStart = true)
actual class CpuDataNativeProvider actual constructor() {

    actual fun initLibrary() {
        cpuinfo_initialize()
    }

    actual fun getCpuName(): String {
        if (!cpuinfo_initialize()) {
            return ""
        }
        return ""
    }

    actual fun hasArmNeon(): Boolean {
        return false
    }

    actual fun getL1dCaches(): IntArray? {
        return null
    }

    actual fun getL1iCaches(): IntArray? {
        return null
    }

    actual fun getL2Caches(): IntArray? {
        return null
    }

    actual fun getL3Caches(): IntArray? {
        return null
    }

    actual fun getL4Caches(): IntArray? {
        return null
    }
}