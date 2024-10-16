package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.getkeepsafe.relinker.ReLinker
import com.kgurgul.cpuinfo.data.provider.CpuDataNativeProvider

class NativeToolsInitializer(
    private val cpuDataNativeProvider: CpuDataNativeProvider,
) {

    fun init(application: Application) {
        ReLinker.loadLibrary(application, "cpuinfo-libs")
        cpuDataNativeProvider.initLibrary()
    }
}
