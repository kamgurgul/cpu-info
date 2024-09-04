package com.kgurgul.cpuinfo.di

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import okio.Path.Companion.toPath
import org.koin.dsl.module
import oshi.SystemInfo

val desktopModule = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { "$USER_PREFERENCES_NAME.preferences_pb".toPath() }
        )
    }
    single { SystemInfo() }
}