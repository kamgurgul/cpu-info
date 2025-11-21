/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.di

import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import com.kgurgul.cpuinfo.data.provider.IosHardwareDataProvider
import com.kgurgul.cpuinfo.data.provider.IosSoftwareDataProvider
import okio.Path.Companion.toPath
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

fun iosModule(
    iosHardwareDataProvider: IosHardwareDataProvider,
    iosSoftwareDataProvider: IosSoftwareDataProvider,
) = module {
    single {
        val documentDirectory: NSURL? =
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
        val path =
            (requireNotNull(documentDirectory).path + "/$USER_PREFERENCES_NAME.preferences_pb")
                .toPath()
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler =
                ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            produceFile = { path },
        )
    }
    single { iosHardwareDataProvider }
    single { iosSoftwareDataProvider }
}
