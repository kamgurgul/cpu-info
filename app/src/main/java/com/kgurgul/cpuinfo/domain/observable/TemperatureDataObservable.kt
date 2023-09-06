package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TemperatureDataObservable @Inject constructor(
    private val dispatchersProvider: IDispatchersProvider,
    private val temperatureProvider: TemperatureProvider,
) : ImmutableInteractor<Unit, List<TemperatureItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        val cpuTempPath = temperatureProvider.findCpuTemperatureLocation()
        while (true) {
            val batteryTemperature = temperatureProvider.getBatteryTemperature()?.let {
                TemperatureItem(R.drawable.ic_battery, R.string.battery, it)
            }
            val cpuTemperature = cpuTempPath?.let { temperatureProvider.getCpuTemp(it) }?.let {
                TemperatureItem(R.drawable.ic_cpu_temp, R.string.cpu, it)
            }
            emit(listOfNotNull(cpuTemperature, batteryTemperature))
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 3000L
    }
}