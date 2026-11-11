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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.battery_charging
import com.kgurgul.cpuinfo.shared.battery_full
import com.kgurgul.cpuinfo.shared.battery_low_power_mode
import com.kgurgul.cpuinfo.shared.battery_state
import com.kgurgul.cpuinfo.shared.battery_unplugged
import com.kgurgul.cpuinfo.shared.cameras
import com.kgurgul.cpuinfo.shared.cellular
import com.kgurgul.cpuinfo.shared.cellular_network_type
import com.kgurgul.cpuinfo.shared.hardware_audio_output_channels
import com.kgurgul.cpuinfo.shared.hardware_audio_sample_rate
import com.kgurgul.cpuinfo.shared.hardware_microphones
import com.kgurgul.cpuinfo.shared.hardware_network_interfaces
import com.kgurgul.cpuinfo.shared.is_charging
import com.kgurgul.cpuinfo.shared.level
import com.kgurgul.cpuinfo.shared.sound_card
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.wireless
import com.kgurgul.cpuinfo.utils.ResourceUtils
import com.kgurgul.cpuinfo.utils.round2
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import org.jetbrains.compose.resources.StringResource
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionPortDescription
import platform.AVFAudio.currentRoute
import platform.AVFAudio.outputNumberOfChannels
import platform.AVFAudio.sampleRate
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDevicePositionFront
import platform.AVFoundation.AVCaptureDevicePositionUnspecified
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInDualWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInLiDARDepthCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInMicrophone
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInTelephotoCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInTripleCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInTrueDepthCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInUltraWideCamera
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.position
import platform.CoreNFC.NFCNDEFReaderSession
import platform.CoreTelephony.CTRadioAccessTechnologyCDMA1x
import platform.CoreTelephony.CTRadioAccessTechnologyCDMAEVDORev0
import platform.CoreTelephony.CTRadioAccessTechnologyCDMAEVDORevA
import platform.CoreTelephony.CTRadioAccessTechnologyCDMAEVDORevB
import platform.CoreTelephony.CTRadioAccessTechnologyEdge
import platform.CoreTelephony.CTRadioAccessTechnologyGPRS
import platform.CoreTelephony.CTRadioAccessTechnologyHSDPA
import platform.CoreTelephony.CTRadioAccessTechnologyHSUPA
import platform.CoreTelephony.CTRadioAccessTechnologyLTE
import platform.CoreTelephony.CTRadioAccessTechnologyNR
import platform.CoreTelephony.CTRadioAccessTechnologyNRNSA
import platform.CoreTelephony.CTRadioAccessTechnologyWCDMA
import platform.CoreTelephony.CTRadioAccessTechnologyeHRPD
import platform.CoreTelephony.CTTelephonyNetworkInfo
import platform.Foundation.NSProcessInfo
import platform.Foundation.lowPowerModeEnabled
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState
import platform.darwin.freeifaddrs
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntop
import platform.posix.AF_INET
import platform.posix.AF_INET6
import platform.posix.INET6_ADDRSTRLEN
import platform.posix.INET_ADDRSTRLEN
import platform.posix.sockaddr_in
import platform.posix.sockaddr_in6

actual class HardwareDataProvider actual constructor() {

    actual suspend fun getData(): List<ItemValue> {
        return buildList {
            val device = UIDevice.currentDevice
            val wasMonitoringEnabled = device.batteryMonitoringEnabled
            device.batteryMonitoringEnabled = true
            add(ItemValue.NameResource(Res.string.battery, ""))
            val batteryLevel = device.batteryLevel
            if (batteryLevel != -1f) {
                val batteryLevelPercentage = (batteryLevel * 100).round2()
                add(ItemValue.NameResource(Res.string.level, "$batteryLevelPercentage%"))
            }
            val batteryState = device.batteryState
            add(
                ItemValue.NameValueResource(Res.string.battery_state, getBatteryState(batteryState))
            )
            val isCharging =
                batteryState == UIDeviceBatteryState.UIDeviceBatteryStateCharging ||
                    batteryState == UIDeviceBatteryState.UIDeviceBatteryStateFull
            add(
                ItemValue.NameValueResource(
                    Res.string.is_charging,
                    ResourceUtils.getYesNoStringResource(isCharging),
                )
            )
            add(
                ItemValue.NameValueResource(
                    Res.string.battery_low_power_mode,
                    ResourceUtils.getYesNoStringResource(
                        NSProcessInfo.processInfo.lowPowerModeEnabled
                    ),
                )
            )
            device.batteryMonitoringEnabled = wasMonitoringEnabled

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
                        val positionLabel =
                            when (camera.position) {
                                AVCaptureDevicePositionBack -> "Back"
                                AVCaptureDevicePositionFront -> "Front"
                                else -> ""
                            }
                        if (positionLabel.isNotEmpty()) {
                            append(positionLabel)
                            append("\n")
                        }
                        append(safeObjCString(camera.uniqueID))
                    }
                    add(ItemValue.Text(safeObjCString(camera.localizedName), description))
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
                        append(safeObjCString(microphone.uniqueID))
                    }
                    add(ItemValue.Text(safeObjCString(microphone.localizedName), description))
                }
            }

            addAll(getAudioSessionInfo())
            addAll(getWirelessAndNetworkInfo())
            addAll(getCellularInfo())
        }
    }

    private fun getBatteryState(batteryState: UIDeviceBatteryState): StringResource {
        return when (batteryState) {
            UIDeviceBatteryState.UIDeviceBatteryStateUnplugged -> Res.string.battery_unplugged

            UIDeviceBatteryState.UIDeviceBatteryStateCharging -> Res.string.battery_charging

            UIDeviceBatteryState.UIDeviceBatteryStateFull -> Res.string.battery_full

            else -> Res.string.unknown
        }
    }

    private fun getCameraDevices(): List<AVCaptureDevice> {
        return AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
                deviceTypes =
                    listOfNotNull(
                        AVCaptureDeviceTypeBuiltInWideAngleCamera,
                        AVCaptureDeviceTypeBuiltInUltraWideCamera,
                        AVCaptureDeviceTypeBuiltInTelephotoCamera,
                        AVCaptureDeviceTypeBuiltInDualCamera,
                        AVCaptureDeviceTypeBuiltInDualWideCamera,
                        AVCaptureDeviceTypeBuiltInTripleCamera,
                        AVCaptureDeviceTypeBuiltInTrueDepthCamera,
                        AVCaptureDeviceTypeBuiltInLiDARDepthCamera,
                    ),
                mediaType = AVMediaTypeVideo,
                position = AVCaptureDevicePositionUnspecified,
            )
            .devices
            .filterIsInstance<AVCaptureDevice>()
    }

    private fun getMicrophoneDevices(): List<AVCaptureDevice> {
        return AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
                deviceTypes = listOf(AVCaptureDeviceTypeBuiltInMicrophone),
                mediaType = AVMediaTypeAudio,
                position = AVCaptureDevicePositionUnspecified,
            )
            .devices
            .filterIsInstance<AVCaptureDevice>()
    }

    private fun getAudioSessionInfo(): List<ItemValue> {
        return buildList {
            val session = AVAudioSession.sharedInstance()
            add(ItemValue.NameResource(Res.string.sound_card, ""))
            val sampleRate = session.sampleRate.toInt()
            if (sampleRate > 0) {
                add(
                    ItemValue.NameResource(
                        Res.string.hardware_audio_sample_rate,
                        "$sampleRate Hz",
                    )
                )
            }
            val outputChannels = session.outputNumberOfChannels.toInt()
            if (outputChannels > 0) {
                add(
                    ItemValue.NameResource(
                        Res.string.hardware_audio_output_channels,
                        outputChannels.toString(),
                    )
                )
            }
            val outputs =
                session.currentRoute.outputs.filterIsInstance<AVAudioSessionPortDescription>()
            outputs.forEach { port ->
                add(
                    ItemValue.Text(
                        safeObjCString(port.portName),
                        safeObjCString(port.portType),
                    )
                )
            }
        }
    }

    private fun getWirelessAndNetworkInfo(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.wireless, ""))
            add(
                ItemValue.ValueResource(
                    "NFC",
                    ResourceUtils.getYesNoStringResource(NFCNDEFReaderSession.readingAvailable),
                )
            )
            val interfaces = getNetworkInterfaces()
            if (interfaces.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.hardware_network_interfaces, ""))
                interfaces.forEach { (name, addresses) ->
                    add(ItemValue.Text(name, addresses.joinToString(separator = "\n")))
                }
            }
        }
    }

    private fun getNetworkInterfaces(): Map<String, List<String>> {
        return memScoped {
            val ifap = allocPointerTo<ifaddrs>()
            if (getifaddrs(ifap.ptr) != 0) {
                return@memScoped emptyMap()
            }
            val result = linkedMapOf<String, MutableList<String>>()
            try {
                var cursor = ifap.value
                while (cursor != null) {
                    val ifa = cursor.pointed
                    val name = ifa.ifa_name?.toKString().orEmpty()
                    val addr = ifa.ifa_addr
                    if (name.isNotEmpty() && addr != null) {
                        val family = addr.pointed.sa_family.toInt()
                        if (family == AF_INET) {
                            val sin = addr.reinterpret<sockaddr_in>().pointed
                            val buf = allocArray<ByteVar>(INET_ADDRSTRLEN)
                            if (
                                inet_ntop(
                                    AF_INET,
                                    sin.sin_addr.ptr,
                                    buf,
                                    INET_ADDRSTRLEN.toUInt(),
                                ) != null
                            ) {
                                val ip = buf.toKString()
                                if (ip.isNotEmpty()) {
                                    result.getOrPut(name) { mutableListOf() }.add(ip)
                                }
                            }
                        } else if (family == AF_INET6) {
                            val sin6 = addr.reinterpret<sockaddr_in6>().pointed
                            val buf = allocArray<ByteVar>(INET6_ADDRSTRLEN)
                            if (
                                inet_ntop(
                                    AF_INET6,
                                    sin6.sin6_addr.ptr,
                                    buf,
                                    INET6_ADDRSTRLEN.toUInt(),
                                ) != null
                            ) {
                                val ip = buf.toKString()
                                if (ip.isNotEmpty()) {
                                    result.getOrPut(name) { mutableListOf() }.add(ip)
                                }
                            }
                        }
                    }
                    cursor = ifa.ifa_next
                }
            } finally {
                freeifaddrs(ifap.value)
            }
            result
        }
    }

    private fun getCellularInfo(): List<ItemValue> {
        val networkInfo = CTTelephonyNetworkInfo()
        val radioTechMap =
            networkInfo.serviceCurrentRadioAccessTechnology
                ?.values
                ?.filterIsInstance<String>()
                ?.mapNotNull { formatRadioAccessTechnology(it) }
                .orEmpty()
        if (radioTechMap.isEmpty()) {
            return emptyList()
        }
        return buildList {
            add(ItemValue.NameResource(Res.string.cellular, ""))
            add(
                ItemValue.NameResource(
                    Res.string.cellular_network_type,
                    radioTechMap.distinct().joinToString(separator = ", "),
                )
            )
        }
    }

    private fun formatRadioAccessTechnology(tech: String): String? {
        return when (tech) {
            CTRadioAccessTechnologyGPRS -> "GPRS (2G)"
            CTRadioAccessTechnologyEdge -> "EDGE (2G)"
            CTRadioAccessTechnologyWCDMA -> "WCDMA (3G)"
            CTRadioAccessTechnologyHSDPA -> "HSDPA (3G)"
            CTRadioAccessTechnologyHSUPA -> "HSUPA (3G)"
            CTRadioAccessTechnologyCDMA1x -> "CDMA 1x (2G)"
            CTRadioAccessTechnologyCDMAEVDORev0 -> "EVDO Rev.0 (3G)"
            CTRadioAccessTechnologyCDMAEVDORevA -> "EVDO Rev.A (3G)"
            CTRadioAccessTechnologyCDMAEVDORevB -> "EVDO Rev.B (3G)"
            CTRadioAccessTechnologyeHRPD -> "eHRPD (3G)"
            CTRadioAccessTechnologyLTE -> "LTE (4G)"
            CTRadioAccessTechnologyNRNSA -> "5G NR NSA"
            CTRadioAccessTechnologyNR -> "5G NR"
            else -> tech.removePrefix("CTRadioAccessTechnology").takeIf { it.isNotEmpty() }
        }
    }

    private fun getFormatedName(name: String?): String {
        return if (name == null || name == "null") {
            ""
        } else {
            name
        }
    }

    /**
     * ObjC interop can deliver null for non-nullable String properties, bypassing Kotlin's type
     * system and causing SIGSEGV when the String is used. Cast to Any? to force a null check at the
     * pointer level.
     */
    @Suppress("USELESS_CAST")
    private fun safeObjCString(value: String?): String = (value as Any?)?.toString() ?: ""
}
