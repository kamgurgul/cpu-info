package com.kgurgul.cpuinfo.di.modules

import com.kgurgul.cpuinfo.appinitializers.AppInitializer
import com.kgurgul.cpuinfo.appinitializers.NativeToolsInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBinds {

    @Binds
    @IntoSet
    abstract fun provideNativeToolsInitializer(bind: NativeToolsInitializer): AppInitializer
}