/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo

import android.app.Application
import com.kgurgul.cpuinfo.appinitializers.AppInitializers
import com.kgurgul.cpuinfo.data.DataModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import javax.inject.Inject

@HiltAndroidApp
class CpuInfoApp : Application() {

    @Inject
    lateinit var initializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        initializers.init(this)
        startKoin {
            modules(
                DataModule().module
            )
        }
    }
}