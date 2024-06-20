package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class GpuDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val gpuDataProvider: GpuDataProvider
) : MutableInteractor<GpuDataObservable.Params, GpuData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        emit(
            GpuData(
                gpuDataProvider.getVulkanVersion(),
                gpuDataProvider.getGlEsVersion(),
                params.glVendor,
                params.glRenderer,
                params.glExtensions
            )
        )
    }

    data class Params(
        val glVendor: String? = null,
        val glRenderer: String? = null,
        val glExtensions: String? = null
    )
}