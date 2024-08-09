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

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_thermostat_24
import com.kgurgul.cpuinfo.utils.round1
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

@Factory
actual class TemperatureProvider actual constructor() : KoinComponent {

    private val appContext: Context by inject()
    private val sensorManager: SensorManager by inject()

    actual val sensorsFlow = callbackFlow {
        val sensorCallback = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySendBlocking(
                    TemperatureItem(
                        id = event.sensor.type,
                        icon = Res.drawable.baseline_thermostat_24,
                        name = event.sensor.name,
                        temperature = event.values[0].round1()
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            }
        }
        supportedSensors.mapNotNull { sensorManager.getDefaultSensor(it) }
            .forEach {
                sensorManager.registerListener(
                    sensorCallback,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        awaitClose { sensorManager.unregisterListener(sensorCallback) }
    }

    actual fun getBatteryTemperature(): Float? {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = appContext.registerReceiver(null, filter)
        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        return if (temp != null && temp != Int.MIN_VALUE) temp / 10f else null
    }

    actual fun findCpuTemperatureLocation(): String? {
        for (location in CPU_TEMP_FILE_PATHS) {
            try {
                val temp = File(location).bufferedReader().use { it.readLine().toDoubleOrNull() }
                val preParsedTemp = temp?.let { if (isTemperatureValid(it)) it else it / 1000.0 }
                if (preParsedTemp != null && isTemperatureValid(preParsedTemp)) {
                    return location
                }
            } catch (e: Exception) {
                // do nothing
            }
        }
        return null
    }

    /**
     * Get temperature for CPU and if needed divided returned value by 1000 to get Celsius unit
     *
     * @return CPU temperature
     */
    actual fun getCpuTemp(path: String): Float? {
        return File(path).bufferedReader().use { it.readLine().toDoubleOrNull() }?.let {
            if (isTemperatureValid(it)) {
                it.toFloat()
            } else {
                (it / 1000).toFloat()
            }
        }
    }

    /**
     * Check if passed temperature is in normal range: -30 - 250 Celsius
     *
     * @param temp current temperature
     */
    private fun isTemperatureValid(temp: Double): Boolean = temp in -30.0..250.0

    companion object {
        // Ugly but currently the easiest working solution is to search well known locations
        // If you know better solution please refactor this :)
        private val CPU_TEMP_FILE_PATHS = listOf(
            "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/i2c-adapter/i2c-4/4-004c/temperature",
            "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature",
            "/sys/devices/platform/omap/omap_temp_sensor.0/temperature",
            "/sys/devices/platform/tegra_tmon/temp1_input",
            "/sys/kernel/debug/tegra_thermal/temp_tj",
            "/sys/devices/platform/s5p-tmu/temperature",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/hwmon/hwmon0/device/temp1_input",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone3/temp",
            "/sys/class/thermal/thermal_zone4/temp",
            "/sys/class/hwmon/hwmonX/temp1_input",
            "/sys/devices/platform/s5p-tmu/curr_temp",
            "/sys/htc/cpu_temp",
            "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/ext_temperature",
            "/sys/devices/platform/tegra-tsensor/tsensor_temperature",
        )

        private val supportedSensors = listOf(
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE,
            GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE,
        )
    }
}