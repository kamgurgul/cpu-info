/*
 * Copyright KG Soft
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
package com.kgurgul.cpuinfo.features.information.hardware

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSProcessInfoPowerStateDidChangeNotification
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryLevelDidChangeNotification
import platform.UIKit.UIDeviceBatteryStateDidChangeNotification

@Composable
actual fun RegisterPowerPlugListener(onRefresh: () -> Unit) {
    val currentOnRefresh by rememberUpdatedState(onRefresh)
    DisposableEffect(Unit) {
        val device = UIDevice.currentDevice
        val wasMonitoringEnabled = device.batteryMonitoringEnabled
        device.batteryMonitoringEnabled = true
        val center = NSNotificationCenter.defaultCenter
        val mainQueue = NSOperationQueue.mainQueue
        val stateObserver =
            center.addObserverForName(
                name = UIDeviceBatteryStateDidChangeNotification,
                `object` = null,
                queue = mainQueue,
            ) {
                currentOnRefresh()
            }
        val levelObserver =
            center.addObserverForName(
                name = UIDeviceBatteryLevelDidChangeNotification,
                `object` = null,
                queue = mainQueue,
            ) {
                currentOnRefresh()
            }
        val powerModeObserver =
            center.addObserverForName(
                name = NSProcessInfoPowerStateDidChangeNotification,
                `object` = null,
                queue = mainQueue,
            ) {
                currentOnRefresh()
            }
        onDispose {
            center.removeObserver(stateObserver)
            center.removeObserver(levelObserver)
            center.removeObserver(powerModeObserver)
            device.batteryMonitoringEnabled = wasMonitoringEnabled
        }
    }
}
