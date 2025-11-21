/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.IGpuDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.extensions
import com.kgurgul.cpuinfo.shared.renderer
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.flow.flow

class GpuDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val gpuDataProvider: IGpuDataProvider,
) : MutableInteractor<GpuDataObservable.Params, List<ItemValue>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        emit(
            buildList {
                addAll(gpuDataProvider.getData())
                if (!params.glVendor.isNullOrEmpty()) {
                    add(ItemValue.NameResource(Res.string.vendor, params.glVendor))
                }
                if (!params.glRenderer.isNullOrEmpty()) {
                    add(ItemValue.NameResource(Res.string.renderer, params.glRenderer))
                }
                if (!params.glExtensions.isNullOrEmpty()) {
                    add(ItemValue.NameResource(Res.string.extensions, params.glExtensions))
                }
            }
        )
    }

    data class Params(
        val glVendor: String? = null,
        val glRenderer: String? = null,
        val glExtensions: String? = null,
    )
}
