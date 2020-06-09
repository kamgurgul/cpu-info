package com.kgurgul.cpuinfo.di.modules

import com.kgurgul.cpuinfo.features.cputile.CpuTileService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeCpuTileService(): CpuTileService
}