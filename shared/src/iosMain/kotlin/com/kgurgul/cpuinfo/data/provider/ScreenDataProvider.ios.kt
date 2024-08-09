package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.height
import com.kgurgul.cpuinfo.shared.screen_brightness
import com.kgurgul.cpuinfo.shared.screen_calibrated_latency
import com.kgurgul.cpuinfo.shared.screen_max_fps
import com.kgurgul.cpuinfo.shared.screen_scale
import com.kgurgul.cpuinfo.shared.width
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIScreen
import platform.darwin.NSObject

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

    actual fun getOrientationFlow(): Flow<String> {
        return callbackFlow {
            trySend(UIDevice.currentDevice.orientation.toString())
            val notificationCenter = NSNotificationCenter.defaultCenter
            val listener = OrientationListener(channel)
            val notificationName = UIDeviceOrientationDidChangeNotification
            notificationCenter.addObserver(
                observer = listener,
                selector = NSSelectorFromString(
                    OrientationListener::orientationDidChange.name + ":"
                ),
                name = notificationName,
                `object` = null,
            )
            UIDevice.currentDevice.beginGeneratingDeviceOrientationNotifications()

            awaitClose {
                notificationCenter.removeObserver(
                    observer = listener,
                    name = notificationName,
                    `object` = null,
                )
                UIDevice.currentDevice.endGeneratingDeviceOrientationNotifications()
            }
        }
    }

    class OrientationListener(private val channel: SendChannel<String>) : NSObject() {

        @OptIn(BetaInteropApi::class)
        @Suppress("UNUSED_PARAMETER")
        @ObjCAction
        fun orientationDidChange(arg: NSNotification) {
            channel.trySend(UIDevice.currentDevice.orientation.toString())
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