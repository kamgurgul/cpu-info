package com.kgurgul.cpuinfo.domain.observable

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_thermostat_24
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.round1
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import javax.inject.Inject

class TemperatureDataObservable @Inject constructor(
    private val dispatchersProvider: IDispatchersProvider,
    private val temperatureProvider: TemperatureProvider,
    private val sensorManager: SensorManager,
) : ImmutableInteractor<Unit, List<TemperatureItem>>() {

    private val supportedSensors = listOf(
        Sensor.TYPE_AMBIENT_TEMPERATURE,
        GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE,
        GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE,
    )

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    private val mainFlow = flow {
        val cpuTempPath = temperatureProvider.findCpuTemperatureLocation()
        while (true) {
            temperatureProvider.getBatteryTemperature()?.let {
                emit(
                    TemperatureItem(
                        id = ID_BATTERY,
                        icon = Res.drawable.ic_battery,
                        name = getString(Res.string.battery),
                        temperature = it
                    )
                )
            }
            cpuTempPath?.let { temperatureProvider.getCpuTemp(it) }?.let {
                emit(
                    TemperatureItem(
                        id = ID_CPU,
                        icon = Res.drawable.ic_cpu_temp,
                        name = getString(Res.string.cpu),
                        temperature = it
                    )
                )
            }
            delay(REFRESH_DELAY)
        }
    }

    private val sensorFlow = callbackFlow {
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

    private val cachedTemperatures = mutableListOf<TemperatureItem>()

    override fun createObservable(params: Unit) = merge(mainFlow, sensorFlow)
        .map { temperatureItem ->
            cachedTemperatures.apply {
                removeAll { it.id == temperatureItem.id }
                add(temperatureItem)
                sortBy { it.id }
            }.toList()
        }

    companion object {
        private const val REFRESH_DELAY = 3000L
        private const val ID_BATTERY = -1
        private const val ID_CPU = -2
        const val GOOGLE_GYRO_TEMPERATURE_SENSOR_TYPE = 65538
        const val GOOGLE_PRESSURE_TEMPERATURE_SENSOR_TYPE = 65539
    }
}