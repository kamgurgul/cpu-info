package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.kgurgul.cpuinfo.utils.isDebugBuild
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : AppInitializer {

    override fun init(application: Application) {
        if (isDebugBuild()) {
            Timber.plant(Timber.DebugTree())
        }
    }
}