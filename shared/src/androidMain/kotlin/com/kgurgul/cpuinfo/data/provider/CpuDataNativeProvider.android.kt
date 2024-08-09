package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Single

@Single(createdAtStart = true)
actual class CpuDataNativeProvider actual constructor() {

    actual external fun initLibrary()

    actual external fun getCpuName(): String

    actual external fun hasArmNeon(): Boolean

    actual external fun getL1dCaches(): IntArray?

    actual external fun getL1iCaches(): IntArray?

    actual external fun getL2Caches(): IntArray?

    actual external fun getL3Caches(): IntArray?

    actual external fun getL4Caches(): IntArray?

    actual external fun getNumberOfCores(): Int
}