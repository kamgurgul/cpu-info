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

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cameras
import com.kgurgul.cpuinfo.shared.hardware_microphones
import com.kgurgul.cpuinfo.shared.unknown
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionUnspecified
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInMicrophone
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInTelephotoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo

@Factory
actual class HardwareDataProvider actual constructor() {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            val cameraDevices = getCameraDevices()
            if (cameraDevices.isNotEmpty()) {
                add(getString(Res.string.cameras) to "")
                cameraDevices.forEach { camera ->
                    val description = getFormatedName(camera.manufacturer) + "\n" + camera.uniqueID
                    add(camera.localizedName to description)
                }
            }
            val microphoneDevices = getMicrophoneDevices()
            if (microphoneDevices.isNotEmpty()) {
                add(getString(Res.string.hardware_microphones) to "")
                microphoneDevices.forEach { microphone ->
                    val description = getFormatedName(microphone.manufacturer) +
                            "\n" + microphone.uniqueID
                    add(microphone.localizedName to description)
                }
            }
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
            position = AVCaptureDevicePositionUnspecified
        ).devices.filterIsInstance<AVCaptureDevice>()
    }

    private fun getMicrophoneDevices(): List<AVCaptureDevice> {
        return AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(
                AVCaptureDeviceTypeBuiltInMicrophone,
            ),
            mediaType = AVMediaTypeAudio,
            position = AVCaptureDevicePositionUnspecified
        ).devices.filterIsInstance<AVCaptureDevice>()
    }

    private suspend fun getFormatedName(name: String?): String {
        return if (name == null || name == "null") {
            getString(Res.string.unknown)
        } else {
            name
        }
    }
}