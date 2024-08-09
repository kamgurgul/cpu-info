package com.kgurgul.cpuinfo.data.provider

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.absolute_height
import com.kgurgul.cpuinfo.shared.absolute_width
import com.kgurgul.cpuinfo.shared.density
import com.kgurgul.cpuinfo.shared.density_class
import com.kgurgul.cpuinfo.shared.dp_height
import com.kgurgul.cpuinfo.shared.dp_width
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.large
import com.kgurgul.cpuinfo.shared.normal
import com.kgurgul.cpuinfo.shared.refresh_rate
import com.kgurgul.cpuinfo.shared.screen_class
import com.kgurgul.cpuinfo.shared.small
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.width
import com.kgurgul.cpuinfo.utils.round2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class ScreenDataProvider actual constructor() : KoinComponent {

    private val resources: Resources by inject()
    private val windowManager: WindowManager by inject()

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getScreenClass())
            add(getDensityClass())
            addAll(getInfoFromDisplayMetrics())
        }
    }

    @Suppress("DEPRECATION")
    actual fun getOrientationFlow(): Flow<String> = flow {
        val display = windowManager.defaultDisplay
        emit(display.rotation.toString())
    }

    private suspend fun getScreenClass(): Pair<String, String> {
        val screenClass: String = when (
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        ) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> getString(Res.string.large)
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> getString(Res.string.normal)
            Configuration.SCREENLAYOUT_SIZE_SMALL -> getString(Res.string.small)
            else -> getString(Res.string.unknown)
        }
        return Pair(getString(Res.string.screen_class), screenClass)
    }

    private suspend fun getDensityClass(): Pair<String, String> {
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
                densityClass = getString(Res.string.unknown)
            }
        }
        return Pair(getString(Res.string.density_class), densityClass)
    }

    @Suppress("DEPRECATION")
    private suspend fun getInfoFromDisplayMetrics(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        try {
            display.getRealMetrics(metrics)
            functionsList.add(
                Pair(
                    getString(Res.string.width),
                    "${metrics.widthPixels}px"
                )
            )
            functionsList.add(
                Pair(
                    getString(Res.string.height),
                    "${metrics.heightPixels}px"
                )
            )

            val density = metrics.density
            val dpHeight = metrics.heightPixels / density
            val dpWidth = metrics.widthPixels / density
            functionsList.add(Pair(getString(Res.string.dp_width), "${dpWidth.toInt()}dp"))
            functionsList.add(
                Pair(
                    getString(Res.string.dp_height),
                    "${dpHeight.toInt()}dp"
                )
            )
            functionsList.add(Pair(getString(Res.string.density), "$density"))
        } catch (e: Exception) {
            // Do nothing
        }

        display.getMetrics(metrics)
        functionsList.add(
            Pair(
                getString(Res.string.absolute_width),
                "${metrics.widthPixels}px"
            )
        )
        functionsList.add(
            Pair(
                getString(Res.string.absolute_height),
                "${metrics.heightPixels}px"
            )
        )

        val refreshRate = display.refreshRate
        functionsList.add(
            Pair(
                getString(Res.string.refresh_rate),
                "${refreshRate.round2()}"
            )
        )

        return functionsList
    }
}