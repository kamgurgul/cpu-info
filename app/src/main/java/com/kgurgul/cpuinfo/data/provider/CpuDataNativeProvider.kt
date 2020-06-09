package com.kgurgul.cpuinfo.data.provider

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CpuDataNativeProvider @Inject constructor() {

    external fun initLibrary()

    external fun getCpuName(): String

    external fun hasArmNeon(): Boolean

    external fun getL1dCaches(): IntArray?

    external fun getL1iCaches(): IntArray?

    external fun getL2Caches(): IntArray?

    external fun getL3Caches(): IntArray?

    external fun getL4Caches(): IntArray?
}