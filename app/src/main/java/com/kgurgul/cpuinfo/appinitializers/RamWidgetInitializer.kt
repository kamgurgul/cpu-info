package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Build
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.ramwidget.RamUsageWidgetProvider
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom ram widget initialization. It will try to create refresh service for ram widget.
 * Currently it will work only on API below 26.
 *
 * @author kgurgul
 */
class RamWidgetInitializer @Inject constructor() : AppInitializer {

    override fun init(application: Application) {
        runOnApiBelow(Build.VERSION_CODES.O) {
            Timber.d("updateRamWidget()")
            val intent = Intent(application, RamUsageWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = intArrayOf(R.xml.ram_widget_provider)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            application.sendBroadcast(intent)
        }
    }
}