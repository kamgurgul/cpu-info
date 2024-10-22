package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class CpuDataNativeProvider actual constructor() : KoinComponent {

    actual fun initLibrary() {
    }

    actual fun getCpuName(): String {
        return "CPU from web"
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

    actual fun getNumberOfCores(): Int {
        return 1
    }
}
