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
import androidx.lifecycle.ViewModel
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import com.kgurgul.cpuinfo.utils.round1
import com.kgurgul.cpuinfo.utils.runOnApiAbove
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for sensors data
 *
 * @author kgurgul
 */
@HiltViewModel
class SensorsInfoViewModel @Inject constructor(
        private val sensorManager: SensorManager
) : ViewModel(), SensorEventListener {

    val listLiveData = ListLiveData<Pair<String, String>>()

    private val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

    @Synchronized
    fun startProvidingData() {
        if (listLiveData.isEmpty()) {
            listLiveData.addAll(sensorList.map { Pair(it.name, " ") })
        }

        // Start register process on new Thread to avoid UI block
        Thread {
            for (sensor in sensorList) {
                sensorManager.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_NORMAL)
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
        updateSensorInfo(event)
    }

    /**
     * Replace sensor value with the new one
     */
    @Synchronized
    private fun updateSensorInfo(event: SensorEvent) {
        val updatedRowId = sensorList.indexOf(event.sensor)
        listLiveData[updatedRowId] = Pair(event.sensor.name, getSensorData(event))
    }

    /**
     * Detect sensor type for passed [SensorEvent] and format it to the correct unit
     */
    @Suppress("DEPRECATION")
    private fun getSensorData(event: SensorEvent): String {
        var data = " "

        val sensorType = event.sensor.type
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GRAVITY, Sensor.TYPE_LINEAR_ACCELERATION ->
                data = "X=${event.values[0].round1()}m/s²  Y=${
                event.values[1].round1()}m/s²  Z=${event.values[2].round1()}m/s²"
            Sensor.TYPE_GYROSCOPE ->
                data = "X=${event.values[0].round1()}rad/s  Y=${
                event.values[1].round1()}rad/s  Z=${event.values[2].round1()}rad/s"
            Sensor.TYPE_ROTATION_VECTOR ->
                data = "X=${event.values[0].round1()}  Y=${
                event.values[1].round1()}  Z=${event.values[2].round1()}"
            Sensor.TYPE_MAGNETIC_FIELD ->
                data = "X=${event.values[0].round1()}μT  Y=${
                event.values[1].round1()}μT  Z=${event.values[2].round1()}μT"
            Sensor.TYPE_ORIENTATION ->
                data = "Azimuth=${event.values[0].round1()}°  Pitch=${
                event.values[1].round1()}°  Roll=${event.values[2].round1()}°"
            Sensor.TYPE_PROXIMITY ->
                data = "Distance=${event.values[0].round1()}cm"
            Sensor.TYPE_AMBIENT_TEMPERATURE ->
                data = "Air temperature=${event.values[0].round1()}°C"
            Sensor.TYPE_LIGHT ->
                data = "Illuminance=${event.values[0].round1()}lx"
            Sensor.TYPE_PRESSURE ->
                data = "Air pressure=${event.values[0].round1()}hPa"
            Sensor.TYPE_RELATIVE_HUMIDITY ->
                data = "Relative humidity=${event.values[0].round1()}%"
            Sensor.TYPE_TEMPERATURE ->
                data = "Device temperature=${event.values[0].round1()}°C"
        }

        // TODO: Multiline support for this kind of data is necessary
        runOnApiAbove(17) {
            when (sensorType) {
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED ->
                    data = "X=${event.values[0].round1()}rad/s  Y=${
                    event.values[1].round1()}rad/s  Z=${
                    event.values[2].round1()}rad/s" /*\nEstimated drift: X=${
                    event.values[3].round1() }rad/s  Y=${
                    event.values[4].round1() }rad/s  Z=${
                    event.values[5].round1() }rad/s"*/
                Sensor.TYPE_GAME_ROTATION_VECTOR ->
                    data = "X=${event.values[0].round1()}  Y=${
                    event.values[1].round1()}  Z=${event.values[2].round1()}"
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED ->
                    data = "X=${event.values[0].round1()}μT  Y=${
                    event.values[1].round1()}μT  Z=${
                    event.values[2].round1()}μT" /*\nIron bias: X=${
                    event.values[3].round1() }μT  Y=${
                    event.values[4].round1() }μT  Z=${
                    event.values[5].round1() }μT"*/
            }
        }

        runOnApiAbove(18) {
            when (sensorType) {
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR ->
                    data = "X=${event.values[0].round1()}  Y=${
                    event.values[1].round1()}  Z=${event.values[2].round1()}"
            }
        }

        return data
    }
}