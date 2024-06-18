package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class CpuDataNativeProvider {

    external fun initLibrary()

    external fun getCpuName(): String

    external fun hasArmNeon(): Boolean

    external fun getL1dCaches(): IntArray?

    external fun getL1iCaches(): IntArray?

    external fun getL2Caches(): IntArray?

    external fun getL3Caches(): IntArray?

    external fun getL4Caches(): IntArray?
}