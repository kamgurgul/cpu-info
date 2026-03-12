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
import android.telephony.TelephonyManager
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
import com.kgurgul.cpuinfo.shared.cellular
import com.kgurgul.cpuinfo.shared.cellular_data_connected
import com.kgurgul.cpuinfo.shared.cellular_data_connecting
import com.kgurgul.cpuinfo.shared.cellular_data_disconnected
import com.kgurgul.cpuinfo.shared.cellular_data_state
import com.kgurgul.cpuinfo.shared.cellular_data_suspended
import com.kgurgul.cpuinfo.shared.cellular_network_country
import com.kgurgul.cpuinfo.shared.cellular_network_type
import com.kgurgul.cpuinfo.shared.cellular_operator
import com.kgurgul.cpuinfo.shared.cellular_phone_type
import com.kgurgul.cpuinfo.shared.cellular_sim_absent
import com.kgurgul.cpuinfo.shared.cellular_sim_count
import com.kgurgul.cpuinfo.shared.cellular_sim_country
import com.kgurgul.cpuinfo.shared.cellular_sim_locked
import com.kgurgul.cpuinfo.shared.cellular_sim_network_locked
import com.kgurgul.cpuinfo.shared.cellular_sim_operator
import com.kgurgul.cpuinfo.shared.cellular_sim_ready
import com.kgurgul.cpuinfo.shared.cellular_sim_state
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
    private val telephonyManager: TelephonyManager by inject()

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
            addAll(getCellularInfo())
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
                    ItemValue.NameResource(Res.string.level, "${batteryPct.round2()}%")
                )
            }

            // Health
            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            if (health != -1) {
                functionsList.add(
                    ItemValue.NameValueResource(
                        Res.string.battery_health,
                        getBatteryHealthStatus(health),
                    )
                )
            }

            // Voltage
            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                functionsList.add(
                    ItemValue.NameResource(Res.string.voltage, "${voltage / 1000.0}V")
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
                )
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
                functionsList.add(ItemValue.NameResource(Res.string.technology, technology))
            }

            // Are we charging / is charged?
            val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            // How we charging?
            val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

            val charging = getYesNoStringResource(isCharging)
            functionsList.add(ItemValue.NameValueResource(Res.string.is_charging, charging))
            if (isCharging) {
                val chargingType =
                    when {
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
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE ->
                Res.string.battery_unspecified_failure

            else -> Res.string.battery_unknown
        }
    }

    /** Get Wi-Fi and Bluetooth mac address and Bluetooth LE support */
    @SuppressLint("InlinedApi")
    private fun getWirelessInfo(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()
        functionsList.add(ItemValue.NameResource(Res.string.wireless, ""))
        // Bluetooth
        functionsList.add(
            ItemValue.NameValueResource(
                Res.string.bluetooth,
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
                ),
            )
        )
        val hasBluetoothLe =
            getYesNoStringResource(
                packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
            )
        functionsList.add(ItemValue.NameValueResource(Res.string.bluetooth_le, hasBluetoothLe))
        // GPS
        functionsList.add(
            ItemValue.ValueResource(
                "GPS",
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
                ),
            )
        )
        // NFC
        functionsList.add(
            ItemValue.ValueResource(
                "NFC",
                getYesNoStringResource(packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)),
            )
        )
        functionsList.add(
            ItemValue.ValueResource(
                "NFC Card Emulation",
                getYesNoStringResource(
                    packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
                ),
            )
        )
        // Wi-Fi
        val hasWiFi = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
        functionsList.add(ItemValue.ValueResource("Wi-Fi", getYesNoStringResource(hasWiFi)))
        if (hasWiFi) {
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Aware",
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
                    ),
                )
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Direct",
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)
                    ),
                )
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi Passpoint",
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_PASSPOINT)
                    ),
                )
            )
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi P2P",
                    getYesNoStringResource(wifiManager.isP2pSupported),
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                functionsList.add(
                    ItemValue.ValueResource(
                        "Wi-Fi 2.4GHz",
                        getYesNoStringResource(wifiManager.is24GHzBandSupported),
                    )
                )
            }
            functionsList.add(
                ItemValue.ValueResource(
                    "Wi-Fi 5GHz",
                    getYesNoStringResource(wifiManager.is5GHzBandSupported),
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                functionsList.add(
                    ItemValue.ValueResource(
                        "Wi-Fi 6GHz (6E)",
                        getYesNoStringResource(wifiManager.is6GHzBandSupported),
                    )
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                functionsList.add(
                    ItemValue.ValueResource(
                        "Wi-Fi 60GHz",
                        getYesNoStringResource(wifiManager.is60GHzBandSupported),
                    )
                )
            }
        }

        try {
            val bluetoothMac =
                android.provider.Settings.Secure.getString(contentResolver, "bluetooth_address")
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
        } catch (_: Exception) {
        }

        // IR
        val irManager =
            appContext.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager?
        val hasIr = irManager?.hasIrEmitter() ?: false
        functionsList.add(
            ItemValue.NameValueResource(Res.string.ir_emitter, getYesNoStringResource(hasIr))
        )

        return functionsList
    }

    @SuppressLint("MissingPermission")
    private fun getCellularInfo(): List<ItemValue> {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            return emptyList()
        }

        return buildList {
            add(ItemValue.NameResource(Res.string.cellular, ""))

            // Phone type
            @Suppress("DEPRECATION") val phoneType = when (telephonyManager.phoneType) {
                TelephonyManager.PHONE_TYPE_GSM -> "GSM"
                TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
                TelephonyManager.PHONE_TYPE_SIP -> "SIP"
                else -> null
            }
            if (phoneType != null) {
                add(ItemValue.NameResource(Res.string.cellular_phone_type, phoneType))
            }

            // Network operator
            val operatorName = telephonyManager.networkOperatorName
            if (!operatorName.isNullOrEmpty()) {
                add(ItemValue.NameResource(Res.string.cellular_operator, operatorName))
            }

            // SIM operator
            val simOperatorName = telephonyManager.simOperatorName
            if (!simOperatorName.isNullOrEmpty()) {
                add(ItemValue.NameResource(Res.string.cellular_sim_operator, simOperatorName))
            }

            // SIM state
            val simStateRes = when (telephonyManager.simState) {
                TelephonyManager.SIM_STATE_READY -> Res.string.cellular_sim_ready
                TelephonyManager.SIM_STATE_ABSENT -> Res.string.cellular_sim_absent
                TelephonyManager.SIM_STATE_PIN_REQUIRED,
                TelephonyManager.SIM_STATE_PUK_REQUIRED -> Res.string.cellular_sim_locked

                TelephonyManager.SIM_STATE_NETWORK_LOCKED ->
                    Res.string.cellular_sim_network_locked

                else -> Res.string.unknown
            }
            add(ItemValue.NameValueResource(Res.string.cellular_sim_state, simStateRes))

            // Network country
            val networkCountry = telephonyManager.networkCountryIso
            if (!networkCountry.isNullOrEmpty()) {
                add(
                    ItemValue.NameResource(
                        Res.string.cellular_network_country,
                        networkCountry.uppercase(),
                    )
                )
            }

            // SIM country
            val simCountry = telephonyManager.simCountryIso
            if (!simCountry.isNullOrEmpty()) {
                add(
                    ItemValue.NameResource(
                        Res.string.cellular_sim_country,
                        simCountry.uppercase(),
                    )
                )
            }

            // MCC/MNC
            val networkOperator = telephonyManager.networkOperator
            if (!networkOperator.isNullOrEmpty() && networkOperator.length >= 5) {
                add(ItemValue.Text("MCC", networkOperator.substring(0, 3)))
                add(ItemValue.Text("MNC", networkOperator.substring(3)))
            }

            // Data state
            val dataStateRes = when (telephonyManager.dataState) {
                TelephonyManager.DATA_CONNECTED -> Res.string.cellular_data_connected
                TelephonyManager.DATA_CONNECTING -> Res.string.cellular_data_connecting
                TelephonyManager.DATA_DISCONNECTED -> Res.string.cellular_data_disconnected
                TelephonyManager.DATA_SUSPENDED -> Res.string.cellular_data_suspended
                else -> Res.string.unknown
            }
            add(ItemValue.NameValueResource(Res.string.cellular_data_state, dataStateRes))

            // Network type (requires READ_PHONE_STATE on API 30-32,
            // auto-granted READ_BASIC_PHONE_STATE on API 33+)
            try {
                val networkType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    telephonyManager.dataNetworkType
                } else {
                    @Suppress("DEPRECATION")
                    telephonyManager.networkType
                }
                val networkTypeName = getNetworkTypeName(networkType)
                if (networkTypeName != null) {
                    add(
                        ItemValue.NameResource(
                            Res.string.cellular_network_type,
                            networkTypeName,
                        )
                    )
                }
            } catch (_: SecurityException) {
                // READ_PHONE_STATE not granted
            }

            // SIM count (dual SIM)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                add(
                    ItemValue.NameResource(
                        Res.string.cellular_sim_count,
                        telephonyManager.activeModemCount.toString(),
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                add(
                    ItemValue.NameResource(
                        Res.string.cellular_sim_count,
                        telephonyManager.phoneCount.toString(),
                    )
                )
            }

            // eSIM support
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(
                    ItemValue.ValueResource(
                        "eSIM",
                        getYesNoStringResource(
                            packageManager.hasSystemFeature(
                                PackageManager.FEATURE_TELEPHONY_EUICC
                            )
                        ),
                    )
                )
            }

            // 5G NR support
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(
                    ItemValue.ValueResource(
                        "5G",
                        getYesNoStringResource(
                            packageManager.hasSystemFeature(
                                "android.hardware.telephony.radio.access.nr"
                            )
                        ),
                    )
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getNetworkTypeName(networkType: Int): String? {
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS (2G)"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2G)"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS (3G)"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA (2G)"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO Rev.0 (3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO Rev.A (3G)"
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT (2G)"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA (3G)"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA (3G)"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
            TelephonyManager.NETWORK_TYPE_IDEN -> "iDEN (2G)"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO Rev.B (3G)"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE (4G)"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD (3G)"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+ (3G)"
            TelephonyManager.NETWORK_TYPE_GSM -> "GSM (2G)"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA (3G)"
            TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN"
            TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
            else -> null
        }
    }

    private fun getUsbInfo(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.hardware_usb, ""))
            add(
                ItemValue.NameValueResource(
                    Res.string.hardware_otg,
                    getYesNoStringResource(
                        packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
                    ),
                )
            )
        }
    }

    /** @return number of the sound card in device - default is 1 */
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

    /** Get available data connected with sound card like ALSA version etc. */
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

    /** Get number, type and orientation of the cameras */
    private fun getCameraInfo(): List<ItemValue> {
        val functionsList = mutableListOf<ItemValue>()

        try {
            val cameraIdList = cameraManager.cameraIdList
            val numberOfCameras = cameraIdList.size
            functionsList.add(ItemValue.NameResource(Res.string.amount, numberOfCameras.toString()))
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

    private fun getYesNoStringResource(yesValue: Boolean) =
        if (yesValue) {
            Res.string.yes
        } else {
            Res.string.no
        }

    /** @return [Intent] with battery information */
    private fun getBatteryStatusIntent(): Intent? {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        return appContext.registerReceiver(null, iFilter)
    }

    /** @return battery capacity from private API. In case of error it will return -1. */
    @SuppressLint("PrivateApi")
    private fun getBatteryCapacity(): Double {
        var capacity = -1.0
        try {
            val powerProfile =
                Class.forName("com.android.internal.os.PowerProfile")
                    .getConstructor(Context::class.java)
                    .newInstance(appContext)
            capacity =
                Class.forName("com.android.internal.os.PowerProfile")
                    .getMethod("getAveragePower", String::class.java)
                    .invoke(powerProfile, "battery.capacity") as Double
        } catch (e: Exception) {
            CpuLogger.e(e) { "Cannot read battery capacity" }
        }
        return capacity
    }
}
