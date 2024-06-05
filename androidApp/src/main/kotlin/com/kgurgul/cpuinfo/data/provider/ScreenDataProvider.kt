package com.kgurgul.cpuinfo.data.provider

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.round2
import javax.inject.Inject

class ScreenDataProvider @Inject constructor(
    private val resources: Resources,
    private val windowManager: WindowManager,
) {

    fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getScreenClass())
            add(getDensityClass())
            addAll(getInfoFromDisplayMetrics())
        }
    }

    private fun getScreenClass(): Pair<String, String> {
        val screenClass: String = when (
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        ) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> resources.getString(R.string.large)
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> resources.getString(R.string.normal)
            Configuration.SCREENLAYOUT_SIZE_SMALL -> resources.getString(R.string.small)
            else -> resources.getString(R.string.unknown)
        }
        return Pair(resources.getString(R.string.screen_class), screenClass)
    }

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
            display.getRealMetrics(metrics)
            functionsList.add(
                Pair(
                    resources.getString(R.string.width),
                    "${metrics.widthPixels}px"
                )
            )
            functionsList.add(
                Pair(
                    resources.getString(R.string.height),
                    "${metrics.heightPixels}px"
                )
            )

            val density = metrics.density
            val dpHeight = metrics.heightPixels / density
            val dpWidth = metrics.widthPixels / density
            functionsList.add(Pair(resources.getString(R.string.dp_width), "${dpWidth.toInt()}dp"))
            functionsList.add(
                Pair(
                    resources.getString(R.string.dp_height),
                    "${dpHeight.toInt()}dp"
                )
            )
            functionsList.add(Pair(resources.getString(R.string.density), "$density"))
        } catch (e: Exception) {
            // Do nothing
        }

        display.getMetrics(metrics)
        functionsList.add(
            Pair(
                resources.getString(R.string.absolute_width),
                "${metrics.widthPixels}px"
            )
        )
        functionsList.add(
            Pair(
                resources.getString(R.string.absolute_height),
                "${metrics.heightPixels}px"
            )
        )

        val refreshRate = display.refreshRate
        functionsList.add(
            Pair(
                resources.getString(R.string.refresh_rate),
                "${refreshRate.round2()}"
            )
        )

        val orientation = display.rotation
        functionsList.add(Pair(resources.getString(R.string.orientation), "$orientation"))

        return functionsList
    }
}