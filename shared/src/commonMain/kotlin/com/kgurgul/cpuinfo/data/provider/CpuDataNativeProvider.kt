package com.kgurgul.cpuinfo.data.provider

expect class CpuDataNativeProvider() : ICpuDataNativeProvider {

    override fun initLibrary()

    override fun getCpuName(): String

    override fun hasArmNeon(): Boolean

    override fun getL1dCaches(): IntArray?

    override fun getL1iCaches(): IntArray?

    override fun getL2Caches(): IntArray?

    override fun getL3Caches(): IntArray?

    override fun getL4Caches(): IntArray?

    override fun getNumberOfCores(): Int
}
