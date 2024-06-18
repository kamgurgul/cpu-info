package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppInitializerComponent : KoinComponent {

    private val nativeToolsInitializer: NativeToolsInitializer by inject()

    fun init(application: Application) {
        nativeToolsInitializer.init(application)
    }
}