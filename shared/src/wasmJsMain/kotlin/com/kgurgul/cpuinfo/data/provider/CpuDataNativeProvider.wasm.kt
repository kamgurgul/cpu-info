package com.kgurgul.cpuinfo.data.provider

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider {

    actual override fun initLibrary() {
    }

    actual override fun getCpuName(): String {
        return "Unknown"
    }

    actual override fun hasArmNeon(): Boolean {
        return false
    }

    actual override fun getL1dCaches(): IntArray? {
        return null
    }

    actual override fun getL1iCaches(): IntArray? {
        return null
    }

    actual override fun getL2Caches(): IntArray? {
        return null
    }

    actual override fun getL3Caches(): IntArray? {
        return null
    }

    actual override fun getL4Caches(): IntArray? {
        return null
    }

    actual override fun getNumberOfCores(): Int {
        return 1
    }
}
