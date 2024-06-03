package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.data.local.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepository: UserPreferencesRepository
    ): IUserPreferencesRepository
}