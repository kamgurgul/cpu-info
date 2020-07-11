package com.kgurgul.cpuinfo.di.modules

import com.kgurgul.cpuinfo.appinitializers.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ApplicationComponent::class)
abstract class AppModuleBinds {

    @Binds
    @IntoSet
    abstract fun provideNativeToolsInitializer(bind: NativeToolsInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideRxInitializer(bind: RxInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideTimberInitializer(bind: TimberInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideRamWidgetInitializer(bind: RamWidgetInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideThemeInitializer(bind: ThemeInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideEpoxyInitializer(bind: EpoxyInitializer): AppInitializer
}