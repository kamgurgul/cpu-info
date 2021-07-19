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

package com.kgurgul.cpuinfo.features.ramwidget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import androidx.preference.PreferenceManager
import com.kgurgul.cpuinfo.features.settings.SettingsFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

/**
 * Really messy implementation to hack refreshing of the ram widget. TBH it should be refactored.
 * With new Android O it should be migrated to the foreground service.
 * </p>
 * Current implementation will hide widget on Android O!
 *
 * @author kgurgul
 */
class RefreshService : Service() {

    companion object {
        const val RAM_BACKGROUND_DELAY = 60000L
        var ramUpdateDelay = 10000L
    }

    private var refreshHandler: Handler? = null
    private lateinit var powerManager: PowerManager
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Timber.d("Service created")

        EventBus.getDefault().register(this)

        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        ramUpdateDelay = prefs.getString(SettingsFragment.KEY_RAM_REFRESHING,
                "10000")!!.toLong()

        refreshHandler = Handler(Looper.getMainLooper())
        refreshHandler?.postDelayed(object : Runnable {
            override fun run() {
                ramUpdateDelay = prefs.getString(SettingsFragment.KEY_RAM_REFRESHING,
                        "10000")!!.toLong()
                val isDeviceActive: Boolean =
                        if (Build.VERSION.SDK_INT >= 20) {
                            powerManager.isInteractive
                        } else {
                            @Suppress("DEPRECATION")
                            powerManager.isScreenOn
                        }

                Timber.d("Device is active: $isDeviceActive")

                if (isDeviceActive) {
                    Timber.d("Request for ram widget update - delay $ramUpdateDelay")
                    val intent = Intent(this@RefreshService,
                            RamUsageWidgetProvider::class.java)
                    intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val widgetId = AppWidgetManager.getInstance(this@RefreshService)
                            .getAppWidgetIds(ComponentName(this@RefreshService,
                                    RamUsageWidgetProvider::class.java))
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetId)
                    this@RefreshService.sendBroadcast(intent)

                    refreshHandler?.postDelayed(this, ramUpdateDelay)
                } else {
                    refreshHandler?.postDelayed(this, RAM_BACKGROUND_DELAY)
                }
            }
        }, ramUpdateDelay)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Add sticky parameter")
        return START_STICKY
    }

    override fun onDestroy() {
        Timber.d("Service destroyed")
        EventBus.getDefault().unregister(this)
        refreshHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe
    fun killServiceEvent(event: KillRefreshServiceEvent) {
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    class KillRefreshServiceEvent
}