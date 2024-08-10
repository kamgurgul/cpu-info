package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.sensors_cpu_temperature
import com.kgurgul.cpuinfo.shared.sensors_cpu_voltage
import com.kgurgul.cpuinfo.shared.sensors_fan_speeds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

@Factory
actual class SensorsInfoProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val temperatureFormatter: TemperatureFormatter by inject()

    actual fun getSensorData(): Flow<List<SensorData>> = flow {
        val sensors = systemInfo.hardware.sensors
        val sensorList = buildList {
            if (!sensors.cpuTemperature.isNaN() && sensors.cpuTemperature != 0.0) {
                add(
                    SensorData(
                        id = CPU_TEMP_ID,
                        name = getString(Res.string.sensors_cpu_temperature),
                        value = temperatureFormatter.format(sensors.cpuTemperature.toFloat())
                    )
                )
            }
            if (sensors.cpuTemperature != 0.0) {
                add(
                    SensorData(
                        id = CPU_VOLTAGE_ID,
                        name = getString(Res.string.sensors_cpu_voltage),
                        value = "${sensors.cpuVoltage}V"
                    )
                )
            }
            if (sensors.fanSpeeds.isNotEmpty()) {
                val fanSpeeds = buildString {
                    sensors.fanSpeeds.forEach {
                        appendLine("${it}RPM")
                    }
                }.trim()
                add(
                    SensorData(
                        id = FAN_SPEED_ID,
                        name = getString(Res.string.sensors_fan_speeds),
                        value = fanSpeeds
                    )
                )
            }
        }
        emit(sensorList)
        delay(REFRESH_DELAY)
    }

    companion object {
        private const val CPU_TEMP_ID = "0"
        private const val CPU_VOLTAGE_ID = "1"
        private const val FAN_SPEED_ID = "2"

        private const val REFRESH_DELAY = 100L
    }
}