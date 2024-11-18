package com.kgurgul.cpuinfo.data.provider

interface ICpuDataNativeProvider {

    fun initLibrary()

    fun getCpuName(): String

    fun hasArmNeon(): Boolean

    fun getL1dCaches(): IntArray?

    fun getL1iCaches(): IntArray?

    fun getL2Caches(): IntArray?

    fun getL3Caches(): IntArray?

    fun getL4Caches(): IntArray?

    fun getNumberOfCores(): Int
}
