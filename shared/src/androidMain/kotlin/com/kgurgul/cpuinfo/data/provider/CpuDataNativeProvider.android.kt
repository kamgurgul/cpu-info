package com.kgurgul.cpuinfo.data.provider

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider {

    actual external override fun initLibrary()

    actual external override fun getCpuName(): String

    actual external override fun hasArmNeon(): Boolean

    actual external override fun getL1dCaches(): IntArray?

    actual external override fun getL1iCaches(): IntArray?

    actual external override fun getL2Caches(): IntArray?

    actual external override fun getL3Caches(): IntArray?

    actual external override fun getL4Caches(): IntArray?

    actual external override fun getNumberOfCores(): Int
}
