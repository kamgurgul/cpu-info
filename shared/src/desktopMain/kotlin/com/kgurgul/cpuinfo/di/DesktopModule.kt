package com.kgurgul.cpuinfo.di

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import net.harawata.appdirs.AppDirsFactory
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import oshi.SystemInfo

val desktopModule = module {
    single {
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() },
            ),
            produceFile = {
                val configPath = AppDirsFactory.getInstance()
                    .getUserConfigDir("CPU-Info", "1.x.x", "kamgurgul")
                "$configPath/$USER_PREFERENCES_NAME.preferences_pb".toPath()
            },
        )
    }
    singleOf(::SystemInfo)
}
