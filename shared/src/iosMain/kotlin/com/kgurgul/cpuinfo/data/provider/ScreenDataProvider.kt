package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.screen_brightness
import com.kgurgul.cpuinfo.shared.screen_calibrated_latency
import com.kgurgul.cpuinfo.shared.screen_max_fps
import com.kgurgul.cpuinfo.shared.screen_scale
import com.kgurgul.cpuinfo.shared.width
import kotlinx.cinterop.useContents
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import platform.UIKit.UIScreen

@Factory
actual class ScreenDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.width) to "${getScreenWidth()}px")
            add(getString(Res.string.height) to "${getScreenHeight()}px")
            add(getString(Res.string.screen_scale) to getScreenScale().toString())
            add(getString(Res.string.screen_brightness) to getScreenBrightness().toString())
            add(
                getString(Res.string.screen_max_fps)
                        to getScreenMaximumFramesPerSecond().toString()
            )
            add(
                getString(Res.string.screen_calibrated_latency)
                        to getScreenCalibratedLatency().toString()
            )
        }
    }

    private fun getScreenWidth(): Int {
        val widthPt = UIScreen.mainScreen().bounds.useContents { size.width }
        return (widthPt * getScreenScale()).toInt()
    }

    private fun getScreenHeight(): Int {
        val heightPt = UIScreen.mainScreen().bounds.useContents { size.height }
        return (heightPt * getScreenScale()).toInt()
    }

    private fun getScreenScale(): Float {
        return UIScreen.mainScreen.scale.toFloat()
    }

    private fun getScreenBrightness(): Float {
        return UIScreen.mainScreen.brightness.toFloat()
    }

    private fun getScreenMaximumFramesPerSecond(): Int {
        return UIScreen.mainScreen.maximumFramesPerSecond.toInt()
    }

    private fun getScreenCalibratedLatency(): Double {
        return UIScreen.mainScreen.calibratedLatency
    }
}