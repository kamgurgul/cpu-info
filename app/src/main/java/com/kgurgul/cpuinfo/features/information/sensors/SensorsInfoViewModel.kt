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

package com.kgurgul.cpuinfo.features.information.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.utils.round1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SensorsInfoViewModel @Inject constructor(
    private val sensorManager: SensorManager
) : ViewModel(), SensorEventListener {

    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

    fun startProvidingData() {
        if (_uiStateFlow.value.sensors.isEmpty()) {
            _uiStateFlow.update { uiState ->
                uiState.copy(
                    sensors = sensorList.map {
                        SensorData(
                            id = it.getUniqueId(),
                            name = it.name,
                            value = " ",
                        )
                    }
                )
            }
        }
        Thread {
            for (sensor in sensorList) {
                if (sensor.reportingMode == Sensor.REPORTING_MODE_ONE_SHOT) {
                    sensorManager.requestTriggerSensor(
                        object : TriggerEventListener() {
                            override fun onTrigger(event: TriggerEvent) {
                                updateSensorInfo(event.sensor, event.values)
                            }
                        },
                        sensor
                    )
                } else {
                    sensorManager.registerListener(
                        this,
                        sensor,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }
        }.start()
    }

    fun stopProvidingData() {
        Thread {
            sensorManager.unregisterListener(this)
        }.start()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent) {
        updateSensorInfo(event.sensor, event.values)
    }

    private fun updateSensorInfo(sensor: Sensor, values: FloatArray) {
        val updatedRowId = sensorList.indexOf(sensor)
        _uiStateFlow.update { uiState ->
            uiState.copy(
                sensors = uiState.sensors.toMutableList().apply {
                    set(
                        updatedRowId,
                        SensorData(
                            id = sensor.getUniqueId(),
                            name = sensor.name,
                            value = getSensorData(sensor.type, values),
                        )
                    )
                }
            )
        }
    }

    /**
     * Detect sensor type for passed [SensorEvent] and format it to the correct unit
     */
    @Suppress("DEPRECATION")
    private fun getSensorData(sensorType: Int, values: FloatArray): String {
        var data = " "

        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LINEAR_ACCELERATION ->
                data = "X=${values[0].round1()}m/s²  Y=${
                    values[1].round1()
                }m/s²  Z=${values[2].round1()}m/s²"

            Sensor.TYPE_GYROSCOPE ->
                data = "X=${values[0].round1()}rad/s  Y=${
                    values[1].round1()
                }rad/s  Z=${values[2].round1()}rad/s"

            Sensor.TYPE_ROTATION_VECTOR ->
                data = "X=${values[0].round1()}  Y=${
                    values[1].round1()
                }  Z=${values[2].round1()}"

            Sensor.TYPE_MAGNETIC_FIELD ->
                data = "X=${values[0].round1()}μT  Y=${
                    values[1].round1()
                }μT  Z=${values[2].round1()}μT"

            Sensor.TYPE_ORIENTATION ->
                data = "Azimuth=${values[0].round1()}°  Pitch=${
                    values[1].round1()
                }°  Roll=${values[2].round1()}°"

            Sensor.TYPE_PROXIMITY ->
                data = "Distance=${values[0].round1()}cm"

            Sensor.TYPE_AMBIENT_TEMPERATURE,
            GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE,
            GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE ->
                data = "Temperature=${values[0].round1()}°C"

            Sensor.TYPE_LIGHT ->
                data = "Illuminance=${values[0].round1()}lx"

            Sensor.TYPE_PRESSURE ->
                data = "Air pressure=${values[0].round1()}hPa"

            Sensor.TYPE_RELATIVE_HUMIDITY ->
                data = "Relative humidity=${values[0].round1()}%"

            Sensor.TYPE_TEMPERATURE ->
                data = "Device temperature=${values[0].round1()}°C"

            Sensor.TYPE_GYROSCOPE_UNCALIBRATED ->
                data = "X=${values[0].round1()}rad/s  Y=${
                    values[1].round1()
                }rad/s  Z=${
                    values[2].round1()
                }rad/s\nEstimated drift: X=${
                    values[3].round1()
                }rad/s  Y=${
                    values[4].round1()
                }rad/s  Z=${
                    values[5].round1()
                }rad/s"

            Sensor.TYPE_GAME_ROTATION_VECTOR ->
                data = "X=${values[0].round1()}  Y=${
                    values[1].round1()
                }  Z=${values[2].round1()}"

            Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED ->
                data = "X=${values[0].round1()}μT  Y=${
                    values[1].round1()
                }μT  Z=${
                    values[2].round1()
                }μT\nIron bias: X=${
                    values[3].round1()
                }μT  Y=${
                    values[4].round1()
                }μT  Z=${
                    values[5].round1()
                }μT"

            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR ->
                data = "X=${values[0].round1()}  Y=${
                    values[1].round1()
                }  Z=${values[2].round1()}"
        }

        return data
    }

    private fun Sensor.getUniqueId(): String {
        return "${this.type}-${this.name}-${this.version}-${this.version}"
    }

    data class UiState(
        val sensors: List<SensorData> = emptyList()
    )
}