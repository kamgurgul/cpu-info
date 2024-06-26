package com.kgurgul.cpuinfo.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

val iosModule = module {
    single {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val path = (
                requireNotNull(documentDirectory).path + "/$USER_PREFERENCES_NAME.preferences_pb"
                ).toPath()
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { path }
        )
    }
}