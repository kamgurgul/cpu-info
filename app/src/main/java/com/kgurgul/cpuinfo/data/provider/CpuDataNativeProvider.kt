package com.kgurgul.cpuinfo.data.provider

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CpuDataNativeProvider @Inject constructor() {

    external fun initLibrary()

    external fun getCpuName(): String
}