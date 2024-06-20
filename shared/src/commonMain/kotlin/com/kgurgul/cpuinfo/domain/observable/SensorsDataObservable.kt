package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.SensorsInfoProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SensorsDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val sensorsInfoProvider: SensorsInfoProvider,
) : ImmutableInteractor<Unit, List<SensorData>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.default

    override fun createObservable(params: Unit): Flow<List<SensorData>> {
        return sensorsInfoProvider.getSensorData()
    }
}