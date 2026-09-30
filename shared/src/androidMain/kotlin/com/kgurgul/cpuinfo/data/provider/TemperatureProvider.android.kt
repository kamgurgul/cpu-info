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

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_thermostat_24
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.shared.temp_thermal_zone
import com.kgurgul.cpuinfo.utils.round1
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class TemperatureProvider actual constructor() : KoinComponent, ITemperatureProvider {

    private val appContext: Context by inject()
    private val sensorManager: SensorManager by inject()

    private val mainTemperaturesFlow: Flow<TemperatureItem> = flow {
        val thermalSources = findThermalSources()
        while (true) {
            getBatteryTemperature()?.let {
                emit(
                    TemperatureItem(
                        id = ID_BATTERY,
                        icon = Res.drawable.ic_battery,
                        name = TextResource.Resource(Res.string.battery),
                        temperature = it,
                    )
                )
            }
            thermalSources.forEach { source ->
                readTemperature(source.tempPath)?.let {
                    emit(
                        TemperatureItem(
                            id = source.id,
                            icon = source.icon,
                            name = source.name,
                            temperature = it,
                        )
                    )
                }
            }
            delay(REFRESH_DELAY.milliseconds)
        }
    }

    private val hardwareSensorsFlow: Flow<TemperatureItem> = callbackFlow {
        val sensorCallback =
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val temp = event.values[0].round1()
                    if (isTemperatureValid(temp.toDouble())) {
                        trySendBlocking(
                            TemperatureItem(
                                id = event.sensor.type,
                                icon = Res.drawable.baseline_thermostat_24,
                                name = TextResource.Text(event.sensor.name),
                                temperature = temp,
                            )
                        )
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
            }
        supportedSensors
            .mapNotNull { sensorManager.getDefaultSensor(it) }
            .forEach {
                sensorManager.registerListener(
                    sensorCallback,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL,
                )
            }
        awaitClose { sensorManager.unregisterListener(sensorCallback) }
    }

    actual override val sensorsFlow: Flow<TemperatureItem> =
        merge(mainTemperaturesFlow, hardwareSensorsFlow)

    actual override fun getBatteryTemperature(): Float? {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = appContext.registerReceiver(null, filter)
        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        return if (temp != null && temp != Int.MIN_VALUE) temp / 10f else null
    }

    private fun findThermalSources(): List<ThermalSource> {
        val sources =
            findIndexedSources(THERMAL_ZONE_DIR, "temp", "type", ID_THERMAL_ZONE_OFFSET)
                .ifEmpty { findIndexedSources(HWMON_DIR, "temp1_input", "name", ID_HWMON_OFFSET) }
                .ifEmpty {
                    findIndexedSources(
                        VIRTUAL_THERMAL_ZONE_DIR,
                        "temp",
                        "type",
                        ID_VIRTUAL_THERMAL_ZONE_OFFSET,
                    )
                }
                .toMutableList()
        if (
            readFirstLine(MEDFIELD_HWMON_NAME_PATH).equals(MEDFIELD_CORETEMP, ignoreCase = true) &&
                readTemperature(MEDFIELD_CORETEMP_PATH) != null
        ) {
            sources +=
                ThermalSource(
                    id = ID_CPU,
                    icon = Res.drawable.ic_cpu_temp,
                    name = TextResource.Resource(Res.string.cpu),
                    tempPath = MEDFIELD_CORETEMP_PATH,
                )
        }
        return sources
    }

    private fun findIndexedSources(
        dirPrefix: String,
        tempFileName: String,
        nameFileName: String,
        idOffset: Int,
    ): List<ThermalSource> =
        (0 until MAX_SENSOR_INDEX).mapNotNull { index ->
            val tempPath = "$dirPrefix$index/$tempFileName"
            if (readTemperature(tempPath) == null) return@mapNotNull null
            val type = readFirstLine("$dirPrefix$index/$nameFileName")
            ThermalSource(
                id = idOffset + index,
                icon = getIconForType(type),
                name =
                    if (type.isNullOrEmpty()) {
                        TextResource.Formatted(Res.string.temp_thermal_zone, listOf(index))
                    } else {
                        TextResource.Text(type)
                    },
                tempPath = tempPath,
            )
        }

    private fun getIconForType(type: String?): DrawableResource {
        val lowerType = type?.lowercase().orEmpty()
        return when {
            CPU_TYPE_KEYWORDS.any { lowerType.contains(it) } -> Res.drawable.ic_cpu_temp
            BATTERY_TYPE_KEYWORDS.any { lowerType.contains(it) } -> Res.drawable.ic_battery
            else -> Res.drawable.baseline_thermostat_24
        }
    }

    private fun readTemperature(path: String): Float? {
        val raw = readFirstLine(path)?.toLongOrNull() ?: return null
        val temp =
            when {
                raw <= 0 -> return null
                raw > 1_500_000 -> if (raw > 20_000_000) return null else raw / 100_000f
                raw > 15_000 -> if (raw > 200_000) return null else raw / 1000f
                raw > 150 -> if (raw > 2000) return null else raw / 10f
                else -> raw.toFloat()
            }
        return temp.round1()
    }

    private fun readFirstLine(path: String): String? =
        try {
            File(path).bufferedReader().use { it.readLine() }?.trim()
        } catch (_: Exception) {
            null
        }

    private fun isTemperatureValid(temp: Double): Boolean = temp in -50.0..250.0

    private data class ThermalSource(
        val id: Int,
        val icon: DrawableResource,
        val name: TextResource,
        val tempPath: String,
    )

    companion object {
        private const val REFRESH_DELAY = 3000L
        private const val ID_BATTERY = -1
        private const val ID_CPU = -2
        private const val ID_THERMAL_ZONE_OFFSET = 1000
        private const val ID_HWMON_OFFSET = 2000
        private const val ID_VIRTUAL_THERMAL_ZONE_OFFSET = 3000
        private const val MAX_SENSOR_INDEX = 100
        private const val GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE = 65538
        private const val GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE = 65539

        private const val THERMAL_ZONE_DIR = "/sys/class/thermal/thermal_zone"
        private const val HWMON_DIR = "/sys/class/hwmon/hwmon"
        private const val VIRTUAL_THERMAL_ZONE_DIR = "/sys/devices/virtual/thermal/thermal_zone"
        private const val MEDFIELD_HWMON_NAME_PATH = "/sys/class/hwmon/hwmon0/device/name"
        private const val MEDFIELD_CORETEMP_PATH = "/sys/class/hwmon/hwmon0/device/soc_temp_input"
        private const val MEDFIELD_CORETEMP = "coretemp"

        private val CPU_TYPE_KEYWORDS = listOf("cpu", "soc", "tsens", "core", "apc")
        private val BATTERY_TYPE_KEYWORDS = listOf("batt", "bms")

        private val supportedSensors =
            listOf(
                Sensor.TYPE_AMBIENT_TEMPERATURE,
                GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE,
                GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE,
            )
    }

    actual override suspend fun isAdminRequired(): Boolean {
        return false
    }
}
