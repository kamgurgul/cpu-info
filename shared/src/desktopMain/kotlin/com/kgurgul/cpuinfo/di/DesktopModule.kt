package com.kgurgul.cpuinfo.di

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import com.kgurgul.cpuinfo.utils.getAppConfigPath
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
                val configPath = getAppConfigPath("CPU-Info")
                "$configPath/$USER_PREFERENCES_NAME.preferences_pb".toPath()
            },
        )
    }
    singleOf(::SystemInfo)
}
