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
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.Camera
import android.os.BatteryManager
import android.os.Build
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.AdapterArrayList
import com.kgurgul.cpuinfo.features.settings.SettingsFragment
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.features.temperature.TemperatureProvider
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.round2
import com.kgurgul.cpuinfo.utils.runOnApiAbove
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
class HardwareInfoViewModel @Inject constructor(
        private val resources: Resources,
        private val temperatureProvider: TemperatureProvider,
        private val temperatureFormatter: TemperatureFormatter,
        private val sharedPreferences: SharedPreferences,
        private val packageManager: PackageManager,
        private val contentResolver: ContentResolver,
        private val batteryStatusProvider: BatteryStatusProvider)
    : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    val dataObservableList = AdapterArrayList<Pair<String, String>>()

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
        if (dataObservableList.isNotEmpty()) {
            dataObservableList.clear()
        }

        dataObservableList.add(Pair(resources.getString(R.string.battery), ""))
        dataObservableList.addAll(getBatteryStatus())

        if (hasCamera()) {
            dataObservableList.add(Pair(resources.getString(R.string.cameras), ""))
            dataObservableList.addAll(getCameraInfo())
        }

        dataObservableList.add(Pair(resources.getString(R.string.sound_card), ""))
        dataObservableList.addAll(getSoundCardInfo())

        val wirelessInfo = getWirelessInfo()
        if (wirelessInfo.size > 0) {
            dataObservableList.add(Pair(resources.getString(R.string.wireless), ""))
            dataObservableList.addAll(wirelessInfo)
        }
    }

    /**
     * Collect information about battery
     */
    private fun getBatteryStatus(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val batteryStatus = batteryStatusProvider.getBatteryStatusIntent()

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
    private fun getWirelessInfo(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        // Bluetooth
        val hasBluetooth = if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            resources.getString(R.string.yes) else resources.getString(R.string.no)
        functionsList.add(Pair(resources.getString(R.string.bluetooth), hasBluetooth))
        runOnApiAbove(Build.VERSION_CODES.JELLY_BEAN_MR1, {
            val hasBluetoothLe =
                    if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
                        resources.getString(R.string.yes) else resources.getString(R.string.no)
            functionsList.add(Pair(resources.getString(R.string.bluetooth_le), hasBluetoothLe))
        })


        val bluetoothMac = android.provider.Settings.Secure.getString(contentResolver,
                "bluetooth_address")
        if (bluetoothMac != null && !bluetoothMac.isEmpty())
            functionsList.add(Pair(resources.getString(R.string.bluetooth_mac), bluetoothMac))

        // Wi-Fi mac
        val filePath = "/sys/class/net/wlan0/address"
        try {
            val reader = RandomAccessFile(filePath, "r")
            val value = reader.readLine()
            reader.close()
            functionsList.add(Pair(resources.getString(R.string.wifi_mac), value))
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
        }

        return functionsList
    }

    /**
     * @return number of the sound card in device - default is 1
     */
    private fun getSoundCardNumber(): Int {
        class AudioFilter : FileFilter {
            override fun accept(pathname: File): Boolean =
                    // http://alsa.opensrc.org/Proc_asound_documentation
                    Pattern.matches("card[0-7]+", pathname.name)
        }

        return try {
            val dir = File("/proc/asound/")
            val files = dir.listFiles(AudioFilter())
            files.size
        } catch (e: Exception) {
            1
        }
    }

    /**
     * Get available data connected with sound card like ALSA version etc.
     */
    private fun getSoundCardInfo(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val soundCardNumber = getSoundCardNumber()
        functionsList.add(Pair(resources.getString(R.string.amount), soundCardNumber.toString()))

        var iterator = 0
        while (iterator < soundCardNumber) {
            functionsList.add(Pair("     ${resources.getString(R.string.card)} $iterator",
                    tryToGetSoundCardId(iterator)))
            iterator++
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
        } catch (e: Exception) {
            // Do nothing
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
        } catch (e: Exception) {
            // Do nothing
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
    private fun getCameraInfo(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val numbersOfCameras = Camera.getNumberOfCameras()
        functionsList.add(Pair(resources.getString(R.string.amount), numbersOfCameras.toString()))

        val cameraName = resources.getString(R.string.camera)
        val cameraType = resources.getString(R.string.type)
        val cameraOrientation = resources.getString(R.string.orientation)
        var iterator = 0
        while (iterator < numbersOfCameras) {
            functionsList.add(Pair("     $cameraName $iterator", " "))

            // Strange error on some SM-G930F with getCameraInfo
            try {
                val info = Camera.CameraInfo()
                Camera.getCameraInfo(iterator, info)
                val type = getCameraType(info)
                functionsList.add(Pair("         $cameraType", type))
                functionsList.add(Pair("         $cameraOrientation", info.orientation.toString()))
            } catch (e: Exception) {
                Timber.e(e)
            }

            iterator++
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