package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import javax.inject.Inject

class ObservableGpuData @Inject constructor(
        dispatchersProvider: DispatchersProvider,
        private val gpuDataProvider: GpuDataProvider
) : ResultInteractor<ObservableGpuData.Params, GpuData>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Params): GpuData {
        return GpuData()
    }

    data class Params(
            val glVendor: String?, val glRenderer: String?, val glExtensions: String?
    )
}