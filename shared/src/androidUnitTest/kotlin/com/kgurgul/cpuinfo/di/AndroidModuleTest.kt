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

    private val androidTestModule = module {
        includes(
            androidModule,
            sharedModule,
        )
    }

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun checkKoinModule() {
        androidTestModule.verify(
            injections = injectedParameters(
                definition<Resources>(
                    AssetManager::class, DisplayMetrics::class, Configuration::class
                ),
                definition<ContentResolver>(Context::class),
                definition<AndroidShortcutManager>(Context::class),
                definition<ApplicationsViewModel>(SavedStateHandle::class),
                definition<ProcessesViewModel>(SavedStateHandle::class),
            )
        )
    }
}
