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

package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.battery_charging
import com.kgurgul.cpuinfo.shared.battery_full
import com.kgurgul.cpuinfo.shared.battery_state
import com.kgurgul.cpuinfo.shared.battery_unplugged
import com.kgurgul.cpuinfo.shared.cameras
import com.kgurgul.cpuinfo.shared.hardware_microphones
import com.kgurgul.cpuinfo.shared.level
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.utils.round2
import org.jetbrains.compose.resources.StringResource
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionUnspecified
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInMicrophone
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInTelephotoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState

actual class HardwareDataProvider actual constructor() {

    actual suspend fun getData(): List<ItemValue> {
        return buildList {
            UIDevice.currentDevice.batteryMonitoringEnabled = true
            add(ItemValue.NameResource(Res.string.battery, ""))
            val batteryLevel = UIDevice.currentDevice.batteryLevel
            if (batteryLevel != -1f) {
                val batteryLevelPercentage = (batteryLevel * 100).round2()
                add(ItemValue.NameResource(Res.string.level, "$batteryLevelPercentage%"))
            }
            add(ItemValue.NameValueResource(Res.string.battery_state, getBatteryState()))
            UIDevice.currentDevice.batteryMonitoringEnabled = false

            val cameraDevices = getCameraDevices()
            if (cameraDevices.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.cameras, ""))
                cameraDevices.forEach { camera ->
                    val description = buildString {
                        val formattedName = getFormatedName(camera.manufacturer)
                        if (formattedName.isNotEmpty()) {
                            append(formattedName)
                            append("\n")
                        }
                        append(camera.uniqueID)
                    }
                    add(ItemValue.Text(camera.localizedName, description))
                }
            }

            val microphoneDevices = getMicrophoneDevices()
            if (microphoneDevices.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.hardware_microphones, ""))
                microphoneDevices.forEach { microphone ->
                    val description = buildString {
                        val formattedName = getFormatedName(microphone.manufacturer)
                        if (formattedName.isNotEmpty()) {
                            append(formattedName)
                            append("\n")
                        }
                        append(microphone.uniqueID)
                    }
                    add(ItemValue.Text(microphone.localizedName, description))
                }
            }
        }
    }

    private fun getBatteryState(): StringResource {
        val batteryState = UIDevice.currentDevice.batteryState
        return when (batteryState) {
            UIDeviceBatteryState.UIDeviceBatteryStateUnplugged ->
                Res.string.battery_unplugged

            UIDeviceBatteryState.UIDeviceBatteryStateCharging ->
                Res.string.battery_charging

            UIDeviceBatteryState.UIDeviceBatteryStateFull ->
                Res.string.battery_full

            else -> Res.string.unknown
        }
    }

    private fun getCameraDevices(): List<AVCaptureDevice> {
        return AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(
                AVCaptureDeviceTypeBuiltInWideAngleCamera,
                AVCaptureDeviceTypeBuiltInUltraWideCamera,
                AVCaptureDeviceTypeBuiltInTelephotoCamera,
            ),
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionUnspecified,
        ).devices.filterIsInstance<AVCaptureDevice>()
    }

    private fun getMicrophoneDevices(): List<AVCaptureDevice> {
        return AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(
                AVCaptureDeviceTypeBuiltInMicrophone,
            ),
            mediaType = AVMediaTypeAudio,
            position = AVCaptureDevicePositionUnspecified,
        ).devices.filterIsInstance<AVCaptureDevice>()
    }

    private fun getFormatedName(name: String?): String {
        return if (name == null || name == "null") {
            ""
        } else {
            name
        }
    }
}
