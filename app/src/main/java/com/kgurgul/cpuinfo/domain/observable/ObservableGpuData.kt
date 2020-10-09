package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ObservableGpuData @Inject constructor(
        dispatchersProvider: DispatchersProvider,
        private val gpuDataProvider: GpuDataProvider
) : MutableInteractor<ObservableGpuData.Params, GpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        Timber.d("Dupa: %s", Thread.currentThread())
        emit(GpuData(
                gpuDataProvider.getGlEsVersion(),
                params.glVendor,
                params.glRenderer,
                params.glExtensions
        ))
    }

    data class Params(
            val glVendor: String? = null,
            val glRenderer: String? = null,
            val glExtensions: String? = null
    )
}