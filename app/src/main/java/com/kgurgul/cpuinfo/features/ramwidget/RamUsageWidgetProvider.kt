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

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.widget.RemoteViews
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.HostActivity
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import com.kgurgul.cpuinfo.widgets.arc.ArcProgress
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.io.RandomAccessFile
import java.util.regex.Pattern

/**
 * Displays current usage of the RAM memory. Won't work on Android O!
 * All refreshing logic can be migrated into foreground service but IMO it is not worth it.
 *
 * @author kgurgul
 */
class RamUsageWidgetProvider : AppWidgetProvider() {

    companion object {
        var previousRamState = -1
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Timber.d("Update called")

        val thisWidget = ComponentName(context, RamUsageWidgetProvider::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        // To ensure that RefreshService is running
        if (!allWidgetIds.isEmpty()) {
            val actualRamState = getUsedPercentageRam(context)

            if (actualRamState != previousRamState) {
                Timber.d("Ram state changed from $previousRamState to $actualRamState")
                previousRamState = actualRamState

                val remoteViews = RemoteViews(context.packageName, R.layout.widget)
                remoteViews.setOnClickPendingIntent(R.id.arc_ram_status, buildButtonPendingIntent(context))
                remoteViews.setImageViewBitmap(R.id.arc_ram_status, getArcBitmap(context))

                pushWidgetUpdates(context.applicationContext, remoteViews)
            }

            // Won't work on Android O!
            runOnApiBelow(Build.VERSION_CODES.O) {
                val intent = Intent(context, RefreshService::class.java)
                context.startService(intent)
            }
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Timber.d("Widget enabled")
        previousRamState = -1
    }

    override fun onDisabled(context: Context) {
        Timber.d("Widget disabled")
        EventBus.getDefault().post(RefreshService.KillRefreshServiceEvent())
        previousRamState = -1

        super.onDisabled(context)
    }

    private fun buildButtonPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, HostActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun pushWidgetUpdates(context: Context, remoteViews: RemoteViews) {
        val ramCleanerWidget = ComponentName(context, RamUsageWidgetProvider::class.java)
        val manager = AppWidgetManager.getInstance(context)
        manager.updateAppWidget(ramCleanerWidget, remoteViews)
    }

    private fun getArcBitmap(context: Context): Bitmap {
        val arcRamProgress = ArcProgress(context)
        arcRamProgress.progress = previousRamState
        arcRamProgress.bottomText = "RAM"
        arcRamProgress.textSize = context.resources.getDimension(R.dimen.arc_text_size)
        arcRamProgress.suffixTextSize = context.resources.getDimension(R.dimen.arc_suffix_text_size)
        arcRamProgress.bottomTextSize = context.resources.getDimension(R.dimen.arc_bottom_text_size)
        arcRamProgress.strokeWidth = context.resources.getDimension(R.dimen.arc_stroke_width)
        arcRamProgress.measure(500, 500)
        arcRamProgress.layout(0, 0, 500, 500)
        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        arcRamProgress.draw(Canvas(bitmap))
        return bitmap
    }

    @Synchronized
    private fun getUsedPercentageRam(context: Context): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam: Long
        totalRam = if (Build.VERSION.SDK_INT >= 16) {
            memoryInfo.totalMem
        } else {
            getTotalRamForOldApi()
        }
        return ((totalRam.toDouble() - memoryInfo.availMem.toDouble()) / totalRam.toDouble()
                * 100.0).toInt()
    }

    private fun getTotalRamForOldApi(): Long {
        var reader: RandomAccessFile? = null
        var totRam: Long = -1
        try {
            reader = RandomAccessFile("/proc/meminfo", "r")
            val load = reader.readLine()

            // Get the Number value from the string
            val p = Pattern.compile("(\\d+)")
            val m = p.matcher(load)
            var value = ""
            while (m.find()) {
                value = m.group(1)!!
            }
            reader.close()

            totRam = value.toLong()
        } catch (ex: Exception) {
            Timber.e(ex)
        } finally {
            reader?.close()
        }

        return totRam * 1024
    }
}