package com.kgurgul.cpuinfo.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.dsl.module
import oshi.SystemInfo

val desktopModule = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { "$USER_PREFERENCES_NAME.preferences_pb".toPath() }
        )
    }
    single { SystemInfo() }
}