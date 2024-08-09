package com.kgurgul.cpuinfo.appinitializers

import com.kgurgul.cpuinfo.data.provider.CpuDataNativeProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppInitializerComponent : KoinComponent {

    private val cpuDataNativeProvider: CpuDataNativeProvider by inject()

    fun doInit() {
        cpuDataNativeProvider.initLibrary()
    }
}