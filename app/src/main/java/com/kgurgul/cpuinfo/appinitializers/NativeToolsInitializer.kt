package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.getkeepsafe.relinker.ReLinker
import com.kgurgul.cpuinfo.data.provider.CpuDataNativeProvider
import javax.inject.Inject

class NativeToolsInitializer @Inject constructor(
        private val cpuDataNativeProvider: CpuDataNativeProvider
) : AppInitializer {

    override fun init(application: Application) {
        ReLinker.loadLibrary(application, "cpuinfo-libs")
        cpuDataNativeProvider.initLibrary()
    }
}