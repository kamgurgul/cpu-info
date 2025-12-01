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
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import com.kgurgul.cpuinfo.utils.MacOSIconDecoder
import com.kgurgul.cpuinfo.utils.getAppConfigPath
import com.kgurgul.cpuinfo.utils.getCachePath
import kotlin.io.path.createDirectories
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import oshi.SystemInfo

val desktopModule = module {
    single(named("image_loader"), createdAtStart = true) {
        SingletonImageLoader.setSafe { context ->
            val cacheDirectory =
                (getCachePath() / "image_cache".toPath()).also { path ->
                    path.toNioPath().createDirectories()
                }
            ImageLoader.Builder(context)
                .components { add(MacOSIconDecoder.Factory()) }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDirectory)
                        .maxSizeBytes(512L * 1024 * 1024)
                        .build()
                }
                .build()
        }
    }
    single {
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler =
                ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            produceFile = {
                val configPath = getAppConfigPath("CPU-Info")
                "$configPath/$USER_PREFERENCES_NAME.preferences_pb".toPath()
            },
        )
    }
    singleOf(::SystemInfo)
}
