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

import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.lifecycle.SavedStateHandle
import com.kgurgul.cpuinfo.features.applications.ApplicationsViewModel
import com.kgurgul.cpuinfo.features.processes.ProcessesViewModel
import com.kgurgul.cpuinfo.utils.AndroidShortcutManager
import kotlin.test.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.definition
import org.koin.test.verify.injectedParameters
import org.koin.test.verify.verify

class AndroidModuleTest {

    private val androidTestModule = module { includes(androidModule, sharedModule) }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkKoinModule() {
        androidTestModule.verify(
            injections =
                injectedParameters(
                    definition<Resources>(
                        AssetManager::class,
                        DisplayMetrics::class,
                        Configuration::class,
                    ),
                    definition<ContentResolver>(Context::class),
                    definition<AndroidShortcutManager>(Context::class),
                    definition<ApplicationsViewModel>(SavedStateHandle::class),
                    definition<ProcessesViewModel>(SavedStateHandle::class),
                )
        )
    }
}
