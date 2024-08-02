package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.GpuDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.extensions
import com.kgurgul.cpuinfo.shared.renderer
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory

@Factory
class GpuDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val gpuDataProvider: GpuDataProvider
) : MutableInteractor<GpuDataObservable.Params, List<Pair<String, String>>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        emit(
            buildList {
                addAll(gpuDataProvider.getData())
                if (!params.glVendor.isNullOrEmpty()) {
                    add(getString(Res.string.vendor) to params.glVendor)
                }
                if (!params.glRenderer.isNullOrEmpty()) {
                    add(getString(Res.string.renderer) to params.glRenderer)
                }
                if (!params.glExtensions.isNullOrEmpty()) {
                    add(getString(Res.string.extensions) to params.glExtensions)
                }
            }
        )
    }

    data class Params(
        val glVendor: String? = null,
        val glRenderer: String? = null,
        val glExtensions: String? = null
    )
}