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

package com.kgurgul.cpuinfo.analytics

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.kgurgul.cpuinfo.R
import timber.log.Timber

/**
 * Implementation of Google Analytics
 *
 * @author kgurgul
 */
class AnalyticsManager(app: Application) {

    private lateinit var tracker: Tracker

    init {
        getDefaultTracker(app)
    }

    /**
     * Set specific screen name
     *
     * @param name screen name
     */
    fun setScreenName(name: String) {
        tracker.setScreenName(name)
        tracker.send(HitBuilders.ScreenViewBuilder().build())
        Timber.d("Screen name set to: %s", name)
    }

    /**
     * Report action from specific category
     *
     * @param category analytics category
     * @param action invoked action
     */
    fun reportEvent(category: String, action: String) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build())
        Timber.d("Report sent. Category: %s, action: %s", category, action)
    }

    /**
     * Report action from specific category
     *
     * @param category analytics category
     * @param action invoked action
     * @param label custom label for specific action
     */
    fun reportEvent(category: String, action: String, label: String) {
        tracker.send(HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build())
        Timber.d("Report sent. Category: %s, action: %s, label: %s", category, action, label)
    }

    @Synchronized
    private fun getDefaultTracker(app: Application) {
        val analytics = GoogleAnalytics.getInstance(app)
        tracker = analytics.newTracker(R.xml.app_tracker)
        Timber.d("Tracker initialized")
        val isInDebug = ((app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0)
        if (isInDebug) {
            // When dry run is set, hits will not be dispatched, but will still be logged as
            // though they were dispatched.
            GoogleAnalytics.getInstance(app).setDryRun(true)
            Timber.d("Google analytics are disabled for debug")
        }
    }
}