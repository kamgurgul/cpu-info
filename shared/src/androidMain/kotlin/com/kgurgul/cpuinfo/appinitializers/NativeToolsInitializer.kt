package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.getkeepsafe.relinker.ReLinker
import com.kgurgul.cpuinfo.data.provider.CpuDataNativeProvider
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
class NativeToolsInitializer(
    private val cpuDataNativeProvider: CpuDataNativeProvider
) {

    fun init(application: Application) {
        ReLinker.loadLibrary(application, "cpuinfo-libs")
        cpuDataNativeProvider.initLibrary()
    }
}