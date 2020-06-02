package com.kgurgul.cpuinfo.domain.observers

import com.kgurgul.cpuinfo.data.provider.CpuDataProvider
import com.kgurgul.cpuinfo.domain.SubjectInteractor
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveCpuData @Inject constructor(
        dispatchersProvider: DispatchersProvider,
        private val cpuDataProvider: CpuDataProvider
) : SubjectInteractor<Unit, CpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val abi = cpuDataProvider.getAbi()
            emit(CpuData(abi))
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 1000L
    }
}