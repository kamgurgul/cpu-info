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

import android.app.Activity
import android.app.Application
import com.kgurgul.cpuinfo.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Base Application class for required initializations
 *
 * @author kgurgul
 */
class CpuInfoApp : Application(), HasActivityInjector {

    companion object {
        lateinit var instance: CpuInfoApp
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        AppInjector.init(this)
    }

    // TODO: Fix widget
/*    fun updateRamWidget(app: Application) {
        Timber.d("Broadcast with ram widget update sent")

        val intent = Intent(app, RamUsageWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(R.xml.ram_widget_provider)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        app.sendBroadcast(intent)
    }*/

    override fun activityInjector(): DispatchingAndroidInjector<Activity>
            = dispatchingAndroidInjector
}