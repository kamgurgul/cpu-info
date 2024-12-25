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
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.amount
import com.kgurgul.cpuinfo.shared.back
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.battery_cold
import com.kgurgul.cpuinfo.shared.battery_dead
import com.kgurgul.cpuinfo.shared.battery_good
import com.kgurgul.cpuinfo.shared.battery_health
import com.kgurgul.cpuinfo.shared.battery_overheat
import com.kgurgul.cpuinfo.shared.battery_overvoltage
import com.kgurgul.cpuinfo.shared.battery_unknown
import com.kgurgul.cpuinfo.shared.battery_unspecified_failure
import com.kgurgul.cpuinfo.shared.bluetooth
import com.kgurgul.cpuinfo.shared.bluetooth_le
import com.kgurgul.cpuinfo.shared.bluetooth_mac
import com.kgurgul.cpuinfo.shared.camera
import com.kgurgul.cpuinfo.shared.camera_external
import com.kgurgul.cpuinfo.shared.camera_lens_number
import com.kgurgul.cpuinfo.shared.cameras
import com.kgurgul.cpuinfo.shared.capacity
import com.kgurgul.cpuinfo.shared.card
import com.kgurgul.cpuinfo.shared.charging_type
import com.kgurgul.cpuinfo.shared.front
import com.kgurgul.cpuinfo.shared.hardware_ac
import com.kgurgul.cpuinfo.shared.hardware_camera_lens_format
import com.kgurgul.cpuinfo.shared.hardware_camera_name_format
import com.kgurgul.cpuinfo.shared.hardware_camera_orientation_format
import com.kgurgul.cpuinfo.shared.hardware_camera_type_format
import com.kgurgul.cpuinfo.shared.hardware_otg
import com.kgurgul.cpuinfo.shared.hardware_sound_card_format
import com.kgurgul.cpuinfo.shared.hardware_usb
import com.kgurgul.cpuinfo.shared.ir_emitter
import com.kgurgul.cpuinfo.shared.is_charging
import com.kgurgul.cpuinfo.shared.level
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.orientation
import com.kgurgul.cpuinfo.shared.sound_card
import com.kgurgul.cpuinfo.shared.technology
import com.kgurgul.cpuinfo.shared.temperature
import com.kgurgul.cpuinfo.shared.type
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.voltage
import com.kgurgul.cpuinfo.shared.wifi_mac
import com.kgurgul.cpuinfo.shared.wireless
import com.kgurgul.cpuinfo.shared.yes
import com.kgurgul.cpuinfo.utils.CpuLogger
import com.kgurgul.cpuinfo.utils.round2
import java.io.File
import java.io.FileFilter
import java.util.regex.Pattern
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class HardwareDataProvider actual constructor() : KoinComponent {

    private val appContext: Context by inject()
    private val temperatureProvider: ITemperatureProvider by inject()
    private val temperatureFormatter: TemperatureFormatter by inject()
    private val packageManager: PackageManager by inject()
    private val contentResolver: ContentResolver by inject()
    private val wifiManager: WifiManager by inject()
    private val cameraManager: CameraManager by inject()

    actual suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.battery, ""))
            addAll(getBatteryStatus())

            if (hasCamera()) {
                add(ItemValue.NameResource(Res.string.cameras, ""))
                addAll(getCameraInfo())
            }

            add(ItemValue.NameResource(Res.string.sound_card, ""))
            addAll(getSoundCardInfo())
            addAll(getWirelessInfo())
            addAll(getUsbInfo())
        }
    }

    private suspend fun getBatteryStatus(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()

        val batteryStatus = getBatteryStatusIntent()
        if (batteryStatus != null) {
            // Level
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (level != -1 && scale != -1) {
                val batteryPct = level / scale.toFloat() * 100.0
                functionsList.add(
                    ItemValue.NameResource(
                        Res.string.level,
                        "${batteryPct.round2()}%",
                    ),
                )
            }

            // Health
            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            if (health != -1) {
                functionsList.add(
                    ItemValue.NameValueResource(
                        Res.string.battery_health,
                        getBatteryHealthStatus(health),
                    ),
                )
            }

            // Voltage
            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                functionsList.add(
                    ItemValue.NameResource(
                        Res.string.voltage,
                        "${voltage / 1000.0}V",
                    ),
                )
            }
        }

        // Temperature
        val temperature = temperatureProvider.getBatteryTemperature()
        if (temperature != null) {
            functionsList.add(
                ItemValue.NameResource(
                    Res.string.temperature,
                    temperatureFormatter.format(temperature),
                ),
            )
        }

        // Capacity
        val capacity = getBatteryCapacity().round2()
        if (capacity != -1.0) {
            functionsList.add(ItemValue.NameResource(Res.string.capacity, "${capacity}mAh"))
        }

        if (batteryStatus != null) {
            // Technology
            val technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            if (!technology.isNullOrEmpty()) {
                functionsList.add(
                    ItemValue.NameResource(Res.string.technology, technology),
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

            val charging = getYesNoStringResource(isCharging)
            functionsList.add(ItemValue.NameValueResource(Res.string.is_charging, charging))
            if (isCharging) {
                val chargingType = when {
                    usbCharge -> Res.string.hardware_usb
                    acCharge -> Res.string.hardware_ac
                    else -> Res.string.unknown
                }
                functionsList.add(
                    ItemValue.NameValueResource(Res.string.charging_type, chargingType)
                )
            }
        }

        return functionsList
    }

    private fun getBatteryHealthStatus(healthInt: Int): StringResource {
        return when (healthInt) {
            BatteryManager.BATTERY_HEALTH_COLD -> Res.string.battery_cold
            BatteryManager.BATTERY_HEALTH_GOOD -> Res.string.battery_good
            BatteryManager.BATTERY_HEALTH_DEAD -> Res.string.battery_dead
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> Res.string.battery_overheat
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> Res.string.battery_overvoltage
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> Res.string.battery_unknown
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> Res.string.battery_unspecified_failure
            else -> Res.string.battery_unknown
        }
    }

    /**
     * Get Wi-Fi and Bluetooth mac address and Bluetooth LE support
     */
    @SuppressLint("InlinedApi")
    private fun getWirelessInfo(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()
        functionsList.add(ItemValue.NameResource(Res.string.wireless, ""))
        // Bluetooth
        functionsList.add(
            ItemValue.NameValueResource(
                Res.string.bluetooth,
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH),
                )
            ),
        )
        val hasBluetoothLe = getYesNoStringResource(
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE),
        )
        functionsList.add(ItemValue.NameValueResource(Res.string.bluetooth_le, hasBluetoothLe))
        // GPS
        functionsList.add(
            ItemValue.ValueResource(
                "GPS",
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS),
                )
            ),
        )
        // NFC
        functionsList.add(
            ItemValue.ValueResource(
                "NFC",
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_NFC),
                )
            ),
        )
        functionsList.add(
            ItemValue.ValueResource(
                "NFC Card Emulation",
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION),
                )
            ),
        )
        // Wi-Fi
        val hasWiFi = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
        functionsList.add(ItemValue.ValueResource("Wi-Fi", getYesNoStringResource(hasWiFi)))
        if (hasWiFi) {
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Aware", getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE),
                    )
                ),
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Direct",
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT),
                    )
                ),
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Passpoint",
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT),
                    )
                )
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi 5Ghz",
                    getYesNoStringResource(wifiManager.is5GHzBandSupported)
                )
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi P2P",
                    getYesNoStringResource(wifiManager.isP2pSupported)
                )
            )
        }

        try {
            val bluetoothMac = android.provider.Settings.Secure.getString(
                contentResolver,
                "bluetooth_address",
            )
            if (bluetoothMac != null && bluetoothMac.isNotEmpty()) {
                functionsList.add(ItemValue.NameResource(Res.string.bluetooth_mac, bluetoothMac))
            }
        } catch (e: Exception) {
            // ignored
        }

        // Wi-Fi mac
        val filePath = "/sys/class/net/wlan0/address"
        try {
            File(filePath).readLines().firstOrNull()?.let {
                functionsList.add(ItemValue.NameResource(Res.string.wifi_mac, it))
            }
        } catch (ignored: Exception) {
        }

        // IR
        val irManager = appContext
            .getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager?
        val hasIr = irManager?.hasIrEmitter() ?: false
        functionsList.add(
            ItemValue.NameValueResource(Res.string.ir_emitter, getYesNoStringResource(hasIr))
        )

        return functionsList
    }

    private fun getUsbInfo(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.hardware_usb, ""))
            add(
                ItemValue.NameValueResource(
                    Res.string.hardware_otg,
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
                    )
                )
            )
        }
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
    private fun getSoundCardInfo(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()

        val soundCardNumber = getSoundCardNumber()
        functionsList.add(ItemValue.NameResource(Res.string.amount, soundCardNumber.toString()))
        for (i in 0 until soundCardNumber) {
            tryToGetSoundCardId(i)?.let {
                functionsList.add(
                    ItemValue.FormattedNameResource(
                        Res.string.hardware_sound_card_format,
                        listOf(Res.string.card, i),
                        it,
                    )
                )
            }
        }
        // ALSA
        val alsa = tryToGetAlsa()
        if (!alsa.isNullOrEmpty()) {
            functionsList.add(ItemValue.Text("ALSA", alsa))
        }

        return functionsList
    }

    private fun tryToGetSoundCardId(cardPosition: Int): String? {
        val filePath = "/proc/asound/card$cardPosition/id"
        return try {
            File(filePath).readLines().firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun tryToGetAlsa(): String? {
        val filePath = "/proc/asound/version"
        return try {
            File(filePath).readLines().firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    private fun hasCamera(): Boolean =
        packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    /**
     * Get number, type and orientation of the cameras
     */
    private fun getCameraInfo(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()

        try {
            val cameraIdList = cameraManager.cameraIdList
            val numberOfCameras = cameraIdList.size
            functionsList.add(
                ItemValue.NameResource(
                    Res.string.amount,
                    numberOfCameras.toString(),
                ),
            )
            for (cameraId in cameraIdList) {
                functionsList.add(
                    ItemValue.FormattedNameResource(
                        Res.string.hardware_camera_name_format,
                        listOf(Res.string.camera, cameraId),
                        " ",
                    )
                )
                try {
                    val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                    val orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    functionsList.add(
                        ItemValue.FormattedNameValueResource(
                            Res.string.hardware_camera_type_format,
                            listOf(Res.string.type),
                            getCameraFacing(facing),
                        )
                    )
                    functionsList.add(
                        ItemValue.FormattedNameResource(
                            Res.string.hardware_camera_orientation_format,
                            listOf(Res.string.orientation),
                            orientation.toString(),
                        )
                    )
                    if (Build.VERSION.SDK_INT >= 28) {
                        val lensAmount = characteristics.physicalCameraIds.size
                        if (lensAmount > 0) {
                            functionsList.add(
                                ItemValue.FormattedNameResource(
                                    Res.string.hardware_camera_orientation_format,
                                    listOf(Res.string.orientation),
                                    orientation.toString(),
                                )
                            )
                            functionsList.add(
                                ItemValue.FormattedNameResource(
                                    Res.string.hardware_camera_lens_format,
                                    listOf(Res.string.camera_lens_number),
                                    lensAmount.toString(),
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
            functionsList.add(ItemValue.NameResource(Res.string.amount, "0"))
        }

        return functionsList
    }

    private fun getCameraFacing(facing: Int?): StringResource =
        when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> Res.string.front
            CameraCharacteristics.LENS_FACING_BACK -> Res.string.back
            CameraCharacteristics.LENS_FACING_EXTERNAL -> Res.string.camera_external
            else -> Res.string.unknown
        }

    private fun getYesNoStringResource(yesValue: Boolean) = if (yesValue) {
        Res.string.yes
    } else {
        Res.string.no
    }

    /**
     * @return [Intent] with battery information
     */
    private fun getBatteryStatusIntent(): Intent? {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return appContext.registerReceiver(null, iFilter)
    }

    /**
     * @return battery capacity from private API. In case of error it will return -1.
     */
    @SuppressLint("PrivateApi")
    private fun getBatteryCapacity(): Double {
        var capacity = -1.0
        try {
            val powerProfile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java).newInstance(appContext)
            capacity = Class
                .forName("com.android.internal.os.PowerProfile")
                .getMethod("getAveragePower", String::class.java)
                .invoke(powerProfile, "battery.capacity") as Double
        } catch (e: Exception) {
            CpuLogger.e(e) { "Cannot read battery capacity" }
        }
        return capacity
    }
}
