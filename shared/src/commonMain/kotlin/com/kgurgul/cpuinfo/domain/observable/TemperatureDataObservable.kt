package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.resources.ILocalResources
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.koin.core.annotation.Factory

@Factory
class TemperatureDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val temperatureProvider: TemperatureProvider,
    private val localResources: ILocalResources,
) : ImmutableInteractor<Unit, List<TemperatureItem>>() {

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
                        name = localResources.getString(Res.string.battery),
                        temperature = it
                    )
                )
            }
            cpuTempPath?.let { temperatureProvider.getCpuTemp(it) }?.let {
                emit(
                    TemperatureItem(
                        id = ID_CPU,
                        icon = Res.drawable.ic_cpu_temp,
                        name = localResources.getString(Res.string.cpu),
                        temperature = it
                    )
                )
            }
            delay(REFRESH_DELAY)
        }
    }

    private val cachedTemperatures = mutableListOf<TemperatureItem>()

    override fun createObservable(params: Unit) = merge(mainFlow, temperatureProvider.sensorsFlow)
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