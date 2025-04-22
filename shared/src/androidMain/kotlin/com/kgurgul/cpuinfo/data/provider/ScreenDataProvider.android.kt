package com.kgurgul.cpuinfo.data.provider

import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import com.kgurgul.cpuinfo.domain.model.ItemValue
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
import com.kgurgul.cpuinfo.shared.screen_density_dpi
import com.kgurgul.cpuinfo.shared.small
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.width
import com.kgurgul.cpuinfo.utils.round2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ScreenDataProvider actual constructor() : IScreenDataProvider, KoinComponent {

    private val resources: Resources by inject()
    private val windowManager: WindowManager by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(getScreenClass())
            addAll(getDensityItems())
            addAll(getInfoFromDisplayMetrics())
        }
    }

    @Suppress("DEPRECATION")
    actual override fun getOrientationFlow(): Flow<String> = flow {
        val display = windowManager.defaultDisplay
        emit(display.rotation.toString())
    }

    private fun getScreenClass(): ItemValue {
        val screenClass = when (
            resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        ) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> Res.string.large
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> Res.string.normal
            Configuration.SCREENLAYOUT_SIZE_SMALL -> Res.string.small
            else -> Res.string.unknown
        }
        return ItemValue.NameValueResource(Res.string.screen_class, screenClass)
    }

    private fun getDensityItems(): List<ItemValue> {
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
            DisplayMetrics.DENSITY_420,
            -> {
                densityClass = "xxhdpi"
            }

            DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_560 -> {
                densityClass = "xxxhdpi"
            }

            else -> {
                densityClass = ""
            }
        }
        return listOf(
            ItemValue.NameResource(Res.string.density_class, densityClass),
            ItemValue.NameResource(Res.string.screen_density_dpi, "$densityDpi dpi"),
        )
    }

    @Suppress("DEPRECATION")
    private fun getInfoFromDisplayMetrics(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        try {
            display.getRealMetrics(metrics)
            functionsList.add(
                ItemValue.NameResource(
                    Res.string.width,
                    "${metrics.widthPixels} px",
                ),
            )
            functionsList.add(
                ItemValue.NameResource(
                    Res.string.height,
                    "${metrics.heightPixels} px",
                ),
            )

            val density = metrics.density
            val dpHeight = metrics.heightPixels / density
            val dpWidth = metrics.widthPixels / density
            functionsList.add(ItemValue.NameResource(Res.string.dp_width, "${dpWidth.toInt()} dp"))
            functionsList.add(
                ItemValue.NameResource(
                    Res.string.dp_height,
                    "${dpHeight.toInt()} dp",
                ),
            )
            functionsList.add(ItemValue.NameResource(Res.string.density, "$density"))
        } catch (_: Exception) {
            // Do nothing
        }

        display.getMetrics(metrics)
        functionsList.add(
            ItemValue.NameResource(
                Res.string.absolute_width,
                "${metrics.widthPixels} px",
            ),
        )
        functionsList.add(
            ItemValue.NameResource(
                Res.string.absolute_height,
                "${metrics.heightPixels} px",
            ),
        )

        val refreshRate = display.refreshRate
        functionsList.add(
            ItemValue.NameResource(
                Res.string.refresh_rate,
                "${refreshRate.round2()} Hz",
            ),
        )

        return functionsList
    }
}
