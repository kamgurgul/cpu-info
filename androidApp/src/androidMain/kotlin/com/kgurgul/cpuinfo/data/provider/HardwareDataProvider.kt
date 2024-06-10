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

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.ConsumerIrManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.utils.CpuLogger
import com.kgurgul.cpuinfo.utils.round2
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile
import java.util.regex.Pattern
import javax.inject.Inject

class HardwareDataProvider @Inject constructor(
    private val resources: Resources,
    private val temperatureProvider: TemperatureProvider,
    private val temperatureFormatter: TemperatureFormatter,
    private val packageManager: PackageManager,
    private val contentResolver: ContentResolver,
    private val batteryStatusProvider: BatteryStatusProvider,
    private val wifiManager: WifiManager,
    private val irManager: ConsumerIrManager?,
    private val cameraManager: CameraManager,
) {

    fun getData(): List<Pair<String, String>> {
        return buildList {
            add(Pair(resources.getString(R.string.battery), ""))
            addAll(getBatteryStatus())

            if (hasCamera()) {
                add(Pair(resources.getString(R.string.cameras), ""))
                addAll(getCameraInfo())
            }

            add(Pair(resources.getString(R.string.sound_card), ""))
            addAll(getSoundCardInfo())
            addAll(getWirelessInfo())
            addAll(getUsbInfo())
        }
    }

    private fun getBatteryStatus(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val batteryStatus = batteryStatusProvider.getBatteryStatusIntent()

        if (batteryStatus != null) {
            // Level
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale != -1) {
                val batteryPct = level / scale.toFloat() * 100.0
                functionsList.add(
                    Pair(
                        resources.getString(R.string.level),
                        "${batteryPct.round2()}%"
                    )
                )
            }

            // Health
            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            if (health != -1) {
                functionsList.add(
                    Pair(
                        resources.getString(R.string.battery_health),
                        getBatteryHealthStatus(health)
                    )
                )
            }

            // Voltage
            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                functionsList.add(
                    Pair(
                        resources.getString(R.string.voltage),
                        "${voltage / 1000.0}V"
                    )
                )
            }
        }

        // Temperature
        val temperature = temperatureProvider.getBatteryTemperature()
        if (temperature != null) {
            functionsList.add(
                Pair(
                    resources.getString(R.string.temperature),
                    temperatureFormatter.format(temperature)
                )
            )
        }

        // Capacity
        val capacity = batteryStatusProvider.getBatteryCapacity().round2()
        if (capacity != -1.0) {
            functionsList.add(Pair(resources.getString(R.string.capacity), "${capacity}mAh"))
        }

        if (batteryStatus != null) {
            // Technology
            val technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            if (!technology.isNullOrEmpty()) {
                functionsList.add(
                    Pair(resources.getString(R.string.technology), technology)
                )
            }

            // Are we charging / is charged?
            val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            // How we charging?
            val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

            val charging =
                if (isCharging) resources.getString(R.string.yes)
                else resources.getString(R.string.no)
            functionsList.add(Pair(resources.getString(R.string.is_charging), charging))
            if (isCharging) {
                val chargingType: String = when {
                    usbCharge -> "USB"
                    acCharge -> "AC"
                    else -> resources.getString(R.string.unknown)
                }
                functionsList.add(Pair(resources.getString(R.string.charging_type), chargingType))
            }
        }

        return functionsList
    }

    private fun getBatteryHealthStatus(healthInt: Int): String {
        return when (healthInt) {
            BatteryManager.BATTERY_HEALTH_COLD -> resources.getString(R.string.battery_cold)
            BatteryManager.BATTERY_HEALTH_GOOD -> resources.getString(R.string.battery_good)
            BatteryManager.BATTERY_HEALTH_DEAD -> resources.getString(R.string.battery_dead)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> resources.getString(R.string.battery_overheat)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> resources.getString(R.string.battery_overvoltage)
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> resources.getString(R.string.battery_unknown)
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> resources.getString(R.string.battery_unspecified_failure)
            else -> resources.getString(R.string.battery_unknown)
        }
    }

    /**
     * Get Wi-Fi and Bluetooth mac address and Bluetooth LE support
     */
    @SuppressLint("InlinedApi")
    private fun getWirelessInfo(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()
        functionsList.add(resources.getString(R.string.wireless) to "")
        // Bluetooth
        functionsList.add(
            resources.getString(R.string.bluetooth) to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
            )
        )
        val hasBluetoothLe = getYesNoString(
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        )
        functionsList.add(resources.getString(R.string.bluetooth_le) to hasBluetoothLe)
        // GPS
        functionsList.add(
            "GPS" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
            )
        )
        // NFC
        functionsList.add(
            "NFC" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)
            )
        )
        functionsList.add(
            "NFC Card Emulation" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
            )
        )
        // Wi-Fi
        val hasWiFi = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
        functionsList.add("Wi-Fi" to getYesNoString(hasWiFi))
        if (hasWiFi) {
            functionsList.add(
                "Wi-Fi Aware" to getYesNoString(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
                )
            )
            functionsList.add(
                "Wi-Fi Direct" to getYesNoString(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)
                )
            )
            functionsList.add(
                "Wi-Fi Passpoint" to getYesNoString(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT)
                )
            )
            functionsList.add("Wi-Fi 5Ghz" to getYesNoString(wifiManager.is5GHzBandSupported))
            functionsList.add("Wi-Fi P2P" to getYesNoString(wifiManager.isP2pSupported))
        }

        try {
            val bluetoothMac = android.provider.Settings.Secure.getString(
                contentResolver,
                "bluetooth_address"
            )
            if (bluetoothMac != null && bluetoothMac.isNotEmpty())
                functionsList.add(resources.getString(R.string.bluetooth_mac) to bluetoothMac)
        } catch (e: Exception) {
            // ignored
        }

        // Wi-Fi mac
        val filePath = "/sys/class/net/wlan0/address"
        try {
            val reader = RandomAccessFile(filePath, "r")
            val value = reader.readLine()
            reader.close()
            functionsList.add(resources.getString(R.string.wifi_mac) to value)
        } catch (ignored: Exception) {
        }

        // IR
        val hasIr = irManager?.hasIrEmitter() ?: false
        functionsList.add(resources.getString(R.string.ir_emitter) to getYesNoString(hasIr))

        return functionsList
    }

    private fun getUsbInfo(): List<Pair<String, String>> {
        val featureList = mutableListOf<Pair<String, String>>()
        featureList.add("USB" to "")
        featureList.add(
            "OTG" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
            )
        )
        return featureList
    }

    /**
     * @return number of the sound card in device - default is 1
     */
    private fun getSoundCardNumber(): Int {
        class AudioFilter : FileFilter {
            // http://alsa.opensrc.org/Proc_asound_documentation
            override fun accept(pathname: File): Boolean =
                Pattern.matches("card[0-7]+", pathname.name)
        }

        return try {
            File("/proc/asound/").listFiles(AudioFilter())!!.size
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Get available data connected with sound card like ALSA version etc.
     */
    private fun getSoundCardInfo(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()

        val soundCardNumber = getSoundCardNumber()
        functionsList.add(Pair(resources.getString(R.string.amount), soundCardNumber.toString()))
        for (i in 0 until soundCardNumber) {
            functionsList.add(
                Pair(
                    "     ${resources.getString(R.string.card)} $i",
                    tryToGetSoundCardId(i)
                )
            )
        }
        // ALSA
        val alsa = tryToGetAlsa()
        if (!alsa.isNullOrEmpty()) {
            functionsList.add(Pair("ALSA", alsa))
        }

        return functionsList
    }

    /**
     * Try to read id of the sound card.
     *
     * @param cardPosition position of the sound card in files
     * @return id from "id" file
     */
    private fun tryToGetSoundCardId(cardPosition: Int): String {
        var id = resources.getString(R.string.unknown)
        val filePath = "/proc/asound/card$cardPosition/id"

        var reader: RandomAccessFile? = null
        try {
            reader = RandomAccessFile(filePath, "r")
            id = reader.readLine()
        } catch (ignored: Exception) {
        } finally {
            reader?.close()
        }

        return id
    }

    /**
     * @return ALSA version if exists, otherwise null
     */
    private fun tryToGetAlsa(): String? {
        var alsa: String? = null
        val filePath = "/proc/asound/version"

        var reader: RandomAccessFile? = null
        try {
            reader = RandomAccessFile(filePath, "r")
            val version = reader.readLine()
            alsa = version
        } catch (ignored: Exception) {
        } finally {
            reader?.close()
        }

        return alsa
    }

    private fun hasCamera(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    /**
     * Get number, type and orientation of the cameras
     */
    private fun getCameraInfo(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()

        try {
            val cameraIdList = cameraManager.cameraIdList
            val numberOfCameras = cameraIdList.size
            functionsList.add(
                Pair(
                    resources.getString(R.string.amount),
                    numberOfCameras.toString()
                )
            )

            val cameraName = resources.getString(R.string.camera)
            val cameraType = resources.getString(R.string.type)
            val cameraOrientation = resources.getString(R.string.orientation)
            for (cameraId in cameraIdList) {
                functionsList.add(Pair("     $cameraName $cameraId", " "))
                try {
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    functionsList.add(Pair("         $cameraType", getCameraFacing(facing)))
                    functionsList.add(Pair("         $cameraOrientation", orientation.toString()))
                    if (Build.VERSION.SDK_INT >= 28) {
                        val lensAmount = characteristics.physicalCameraIds.size
                        if (lensAmount > 0) {
                            functionsList.add(
                                Pair(
                                    "         ${resources.getString(R.string.camera_lens_number)}",
                                    lensAmount.toString()
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    CpuLogger.e(e) { "Cannot read camera characteristics" }
                }
            }
        } catch (e: Exception) {
            CpuLogger.e(e) { "Cannot read camera list" }
            functionsList.add(Pair(resources.getString(R.string.amount), "0"))
        }

        return functionsList
    }

    private fun getCameraFacing(facing: Int?): String =
        when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT ->
                resources.getString(R.string.front)

            CameraCharacteristics.LENS_FACING_BACK ->
                resources.getString(R.string.back)

            CameraCharacteristics.LENS_FACING_EXTERNAL ->
                resources.getString(R.string.camera_external)

            else -> resources.getString(R.string.unknown)
        }

    private fun getYesNoString(yesValue: Boolean) = if (yesValue) {
        resources.getString(R.string.yes)
    } else {
        resources.getString(R.string.no)
    }
}