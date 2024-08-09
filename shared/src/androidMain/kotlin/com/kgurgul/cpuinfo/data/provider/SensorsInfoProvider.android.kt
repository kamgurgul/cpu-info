package com.kgurgul.cpuinfo.data.provider

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.domain.observable.TemperatureDataObservable.Companion.GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE
import com.kgurgul.cpuinfo.utils.round1
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Factory
actual class SensorsInfoProvider actual constructor() : KoinComponent {

    private val sensorManager: SensorManager by inject()

    actual fun getSensorData(): Flow<List<SensorData>> = callbackFlow {
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val initialData = sensorList.map {
            SensorData(
                id = it.getUniqueId(),
                name = it.name,
                value = " ",
            )
        }
        trySend(initialData)
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val updatedSensor = listOf(
                    SensorData(
                        id = event.sensor.getUniqueId(),
                        name = event.sensor.name,
                        value = getSensorData(event.sensor.type, event.values)
                    )
                )
                trySend(updatedSensor)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used
            }
        }
        for (sensor in sensorList) {
            if (sensor.reportingMode == Sensor.REPORTING_MODE_ONE_SHOT) {
                sensorManager.requestTriggerSensor(
                    object : TriggerEventListener() {
                        override fun onTrigger(event: TriggerEvent) {
                            val updatedSensor = listOf(
                                SensorData(
                                    id = event.sensor.getUniqueId(),
                                    name = event.sensor.name,
                                    value = getSensorData(event.sensor.type, event.values)
                                )
                            )
                            trySend(updatedSensor)
                        }
                    },
                    sensor
                )
            } else {
                sensorManager.registerListener(
                    sensorListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
        awaitClose { sensorManager.unregisterListener(sensorListener) }
    }

    private fun Sensor.getUniqueId(): String {
        return "${this.type}-${this.name}-${this.version}-${this.version}"
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
}