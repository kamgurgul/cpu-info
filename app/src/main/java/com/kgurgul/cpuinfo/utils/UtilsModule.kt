package com.kgurgul.cpuinfo.utils

import com.kgurgul.cpuinfo.utils.preferences.IPrefs
import com.kgurgul.cpuinfo.utils.preferences.Prefs
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatchersModule {
    @Binds
    abstract fun bindDispatchersProvider(provider: DefaultDispatchersProvider): IDispatchersProvider

    @Binds
    abstract fun bindPrefs(prefs: Prefs): IPrefs
}