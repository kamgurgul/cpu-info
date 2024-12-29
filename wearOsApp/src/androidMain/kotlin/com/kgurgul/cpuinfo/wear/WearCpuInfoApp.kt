package com.kgurgul.cpuinfo.wear

import android.app.Application
import com.kgurgul.cpuinfo.appinitializers.AppInitializerComponent
import com.kgurgul.cpuinfo.di.androidModule
import com.kgurgul.cpuinfo.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WearCpuInfoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WearCpuInfoApp)
            modules(
                androidModule,
                sharedModule,
            )
        }
        AppInitializerComponent().init(this)
    }
}
