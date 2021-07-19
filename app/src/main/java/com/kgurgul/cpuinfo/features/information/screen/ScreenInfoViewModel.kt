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

package com.kgurgul.cpuinfo.features.information.screen

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import com.kgurgul.cpuinfo.utils.round2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel which is responsible for screen details
 *
 * @author kgurgul
 */
@HiltViewModel
class ScreenInfoViewModel @Inject constructor(
        private val resources: Resources,
        private val windowManager: WindowManager
) : ViewModel() {

    val listLiveData = ListLiveData<Pair<String, String>>()

    init {
        getScreenData()
    }

    /**
     * Get all screen details
     */
    private fun getScreenData() {
        if (listLiveData.isNotEmpty()) {
            listLiveData.clear()
        }
        listLiveData.add(getScreenClass())
        listLiveData.add(getDensityClass())
        listLiveData.addAll(getInfoFromDisplayMetrics())
    }

    /**
     * Classify screen into three main classes: large, normal, small
     */
    private fun getScreenClass(): Pair<String, String> {
        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        val screenClass: String

        screenClass = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> resources.getString(R.string.large)
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> resources.getString(R.string.normal)
            Configuration.SCREENLAYOUT_SIZE_SMALL -> resources.getString(R.string.small)
            else -> resources.getString(R.string.unknown)
        }

        return Pair(resources.getString(R.string.screen_class), screenClass)
    }

    /**
     * Classify screen into density classes: ldpi, mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
     */
    private fun getDensityClass(): Pair<String, String> {
        val densityDpi = resources.displayMetrics.densityDpi

        val densityClass: String
        when (densityDpi) {
            DisplayMetrics.DENSITY_LOW -> {
                densityClass = "ldpi"
            }

            DisplayMetrics.DENSITY_MEDIUM -> {
                densityClass = "mdpi"
            }

            DisplayMetrics.DENSITY_TV, DisplayMetrics.DENSITY_HIGH -> {
                densityClass = "hdpi"
            }

            DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_280 -> {
                densityClass = "xhdpi"
            }

            DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_360, DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420 -> {
                densityClass = "xxhdpi"
            }

            DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_560 -> {
                densityClass = "xxxhdpi"
            }

            else -> {
                densityClass = resources.getString(R.string.unknown)
            }
        }

        return Pair(resources.getString(R.string.density_class), densityClass)
    }

    @Suppress("DEPRECATION")
    private fun getInfoFromDisplayMetrics(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()

        val display = windowManager.defaultDisplay

        val metrics = DisplayMetrics()
        try {
            if (Build.VERSION.SDK_INT >= 17) {
                display.getRealMetrics(metrics)
                functionsList.add(Pair(resources.getString(R.string.width), "${metrics.widthPixels}px"))
                functionsList.add(Pair(resources.getString(R.string.height), "${metrics.heightPixels}px"))
            } else {
                val getRawH = Display::class.java.getMethod("getRawHeight")
                val getRawW = Display::class.java.getMethod("getRawWidth")

                val height = getRawH.invoke(display) as Int
                val width = getRawW.invoke(display) as Int
                functionsList.add(Pair(resources.getString(R.string.width), "${width}px"))
                functionsList.add(Pair(resources.getString(R.string.height), "${height}px"))
            }

            val density = metrics.density
            val dpHeight = metrics.heightPixels / density
            val dpWidth = metrics.widthPixels / density

            functionsList.add(Pair(resources.getString(R.string.dp_width), "${dpWidth.toInt()}dp"))
            functionsList.add(Pair(resources.getString(R.string.dp_height), "${dpHeight.toInt()}dp"))
            functionsList.add(Pair(resources.getString(R.string.density), "$density"))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        display.getMetrics(metrics)
        functionsList.add(Pair(resources.getString(R.string.absolute_width), "${metrics.widthPixels}px"))
        functionsList.add(Pair(resources.getString(R.string.absolute_height), "${metrics.heightPixels}px"))

        val refreshRate = display.refreshRate
        functionsList.add(Pair(resources.getString(R.string.refresh_rate), "${refreshRate.round2()}"))

        val orientation = display.rotation
        functionsList.add(Pair(resources.getString(R.string.orientation), "$orientation"))

        return functionsList
    }
}