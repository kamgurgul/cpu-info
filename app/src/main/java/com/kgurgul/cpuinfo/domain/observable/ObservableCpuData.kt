package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.CpuDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObservableCpuData @Inject constructor(
        dispatchersProvider: DispatchersProvider,
        private val cpuDataProvider: CpuDataProvider
) : ImmutableInteractor<Unit, CpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val abi = cpuDataProvider.getAbi()
            val coreNumber = cpuDataProvider.getNumberOfCores()
            val frequencies = mutableListOf<CpuData.Frequency>()
            for (i in 0 until coreNumber) {
                val (min, max) = cpuDataProvider.getMinMaxFreq(i)
                val current = cpuDataProvider.getCurrentFreq(i)
                frequencies.add(CpuData.Frequency(min, max, current))
            }
            emit(CpuData(abi, coreNumber, frequencies))
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 1000L
    }
}