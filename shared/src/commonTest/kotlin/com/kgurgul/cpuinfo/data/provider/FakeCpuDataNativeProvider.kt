package com.kgurgul.cpuinfo.data.provider

class FakeCpuDataNativeProvider : ICpuDataNativeProvider {

    override fun initLibrary() {
    }

    override fun getCpuName(): String {
        return "CPU_NAME"
    }

    override fun hasArmNeon(): Boolean {
        return true
    }

    override fun getL1dCaches(): IntArray? {
        return null
    }

    override fun getL1iCaches(): IntArray? {
        return null
    }

    override fun getL2Caches(): IntArray? {
        return null
    }

    override fun getL3Caches(): IntArray? {
        return null
    }

    override fun getL4Caches(): IntArray? {
        return null
    }

    override fun getNumberOfCores(): Int {
        return 1
    }
}
