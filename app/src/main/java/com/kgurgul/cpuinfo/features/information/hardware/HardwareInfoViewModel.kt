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

@file:Suppress("DEPRECATION")

package com.kgurgul.cpuinfo.features.information.hardware

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.Camera
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.settings.SettingsFragment
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.features.temperature.TemperatureProvider
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import com.kgurgul.cpuinfo.utils.round2
import com.kgurgul.cpuinfo.utils.runOnApiAbove
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * ViewModel for [HardwareInfoFragment]
 *
 * @author kgurgul
 */
@HiltViewModel
class HardwareInfoViewModel @Inject constructor(
        private val resources: Resources,
        private val temperatureProvider: TemperatureProvider,
        private val temperatureFormatter: TemperatureFormatter,
        private val sharedPreferences: SharedPreferences,
        private val packageManager: PackageManager,
        private val contentResolver: ContentResolver,
        private val batteryStatusProvider: BatteryStatusProvider,
        private val wifiManager: WifiManager
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    val listLiveData = ListLiveData<Pair<String, String>>()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        refreshHardwareInfo()
    }

    /**
     * Refresh all info connected with hardware like: battery, wireless connection (Wi-Fi,
     * Bluetooth), sound card and camera
     */
    @Synchronized
    fun refreshHardwareInfo() {
        if (listLiveData.isNotEmpty()) {
            listLiveData.clear()
        }

        listLiveData.add(Pair(resources.getString(R.string.battery), ""))
        listLiveData.addAll(getBatteryStatus())

        if (hasCamera()) {
            listLiveData.add(Pair(resources.getString(R.string.cameras), ""))
            listLiveData.addAll(getCameraInfo())
        }

        listLiveData.add(Pair(resources.getString(R.string.sound_card), ""))
        listLiveData.addAll(getSoundCardInfo())
        listLiveData.addAll(getWirelessInfo())
        listLiveData.addAll(getUsbInfo())
    }

    /**
     * Collect information about battery
     */
    private fun getBatteryStatus(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val batteryStatus = batteryStatusProvider.getBatteryStatusIntent()

        if (batteryStatus != null) {
            // Level
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale != -1) {
                val batteryPct = level / scale.toFloat() * 100.0
                functionsList.add(Pair(resources.getString(R.string.level), "${batteryPct.round2()}%"))
            }

            // Health
            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            if (health != -1) {
                functionsList.add(Pair(resources.getString(R.string.battery_health),
                        getBatteryHealthStatus(health)))
            }

            // Voltage
            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                functionsList.add(Pair(resources.getString(R.string.voltage), "${voltage / 1000.0}V"))
            }
        }

        // Temperature
        val temperature = temperatureProvider.getBatteryTemperature()
        if (temperature > 0) {
            functionsList.add(Pair(resources.getString(R.string.temperature),
                    temperatureFormatter.format(temperature.toFloat())))
        }

        // Capacity
        val capacity = batteryStatusProvider.getBatteryCapacity().round2()
        if (capacity != -1.0) {
            functionsList.add(Pair(resources.getString(R.string.capacity), "${capacity}mAh"))
        }

        if (batteryStatus != null) {
            // Technology
            val technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            Utils.addPairIfExists(functionsList, resources.getString(R.string.technology), technology)

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

    /**
     * @return battery health status as a string
     */
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
        functionsList.add(resources.getString(R.string.bluetooth) to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
        )
        runOnApiAbove(Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val hasBluetoothLe = getYesNoString(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
            )
            functionsList.add(resources.getString(R.string.bluetooth_le) to hasBluetoothLe)
        }
        // GPS
        functionsList.add("GPS" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS))
        )
        // NFC
        functionsList.add("NFC" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_NFC))
        )
        functionsList.add("NFC Card Emulation" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION))
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
            if (Build.VERSION.SDK_INT >= 21) {
                functionsList.add("Wi-Fi 5Ghz" to getYesNoString(wifiManager.is5GHzBandSupported))
                functionsList.add("Wi-Fi P2P" to getYesNoString(wifiManager.isP2pSupported))
            }
        }

        val bluetoothMac = android.provider.Settings.Secure.getString(contentResolver,
                "bluetooth_address")
        if (bluetoothMac != null && bluetoothMac.isNotEmpty())
            functionsList.add(resources.getString(R.string.bluetooth_mac) to bluetoothMac)

        // Wi-Fi mac
        val filePath = "/sys/class/net/wlan0/address"
        try {
            val reader = RandomAccessFile(filePath, "r")
            val value = reader.readLine()
            reader.close()
            functionsList.add(resources.getString(R.string.wifi_mac) to value)
        } catch (ignored: Exception) {
        }

        return functionsList
    }

    private fun getUsbInfo(): List<Pair<String, String>> {
        val featureList = mutableListOf<Pair<String, String>>()
        featureList.add("USB" to "")
        featureList.add("OTG" to getYesNoString(
                packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST))
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
            functionsList.add(Pair("     ${resources.getString(R.string.card)} $i", tryToGetSoundCardId(i)))
        }
        // ALSA
        val alsa = tryToGetAlsa()
        Utils.addPairIfExists(functionsList, "ALSA", alsa)

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

    /**
     * @return true if device has at least 1 camera, otherwise false
     */
    private fun hasCamera(): Boolean =
            packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    /**
     * Get number, type and orientation of the cameras
     */
    @Suppress("DEPRECATION")
    private fun getCameraInfo(): List<Pair<String, String>> {
        val functionsList = mutableListOf<Pair<String, String>>()

        val numbersOfCameras = Camera.getNumberOfCameras()
        functionsList.add(Pair(resources.getString(R.string.amount), numbersOfCameras.toString()))

        val cameraName = resources.getString(R.string.camera)
        val cameraType = resources.getString(R.string.type)
        val cameraOrientation = resources.getString(R.string.orientation)
        for (i in 0 until numbersOfCameras) {
            functionsList.add(Pair("     $cameraName $i", " "))
            try {
                val info = Camera.CameraInfo()
                Camera.getCameraInfo(i, info)
                val type = getCameraType(info)
                functionsList.add(Pair("         $cameraType", type))
                functionsList.add(Pair("         $cameraOrientation", info.orientation.toString()))
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        return functionsList
    }

    /**
     * Detect camera type using old API
     */
    private fun getCameraType(info: Camera.CameraInfo): String =
            when (info.facing) {
                Camera.CameraInfo.CAMERA_FACING_FRONT -> resources.getString(R.string.front)
                Camera.CameraInfo.CAMERA_FACING_BACK -> resources.getString(R.string.back)
                else -> resources.getString(R.string.unknown)
            }

    private fun getYesNoString(yesValue: Boolean) = if (yesValue) {
        resources.getString(R.string.yes)
    } else {
        resources.getString(R.string.no)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == SettingsFragment.KEY_TEMPERATURE_UNIT) {
            refreshHardwareInfo()
        }
    }

    override fun onCleared() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onCleared()
    }
}