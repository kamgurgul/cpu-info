package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.screen_brightness
import com.kgurgul.cpuinfo.shared.screen_calibrated_latency
import com.kgurgul.cpuinfo.shared.screen_max_fps
import com.kgurgul.cpuinfo.shared.screen_scale
import com.kgurgul.cpuinfo.shared.width
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class ScreenDataProvider actual constructor() : KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(
                Pair(
                    getString(Res.string.width),
                    "${iosHardwareDataProvider.getScreenWidth()}px"
                )
            )
            add(
                Pair(
                    getString(Res.string.height),
                    "${iosHardwareDataProvider.getScreenHeight()}px"
                )
            )
            add(
                Pair(
                    getString(Res.string.screen_scale),
                    iosHardwareDataProvider.getScreenScale().toString()
                )
            )
            add(
                Pair(
                    getString(Res.string.screen_brightness),
                    iosHardwareDataProvider.getScreenBrightness().toString()
                )
            )
            add(
                Pair(
                    getString(Res.string.screen_max_fps),
                    iosHardwareDataProvider.getScreenMaximumFramesPerSecond().toString()
                )
            )
            add(
                Pair(
                    getString(Res.string.screen_calibrated_latency),
                    iosHardwareDataProvider.getScreenCalibratedLatency().toString()
                )
            )
        }
    }
}