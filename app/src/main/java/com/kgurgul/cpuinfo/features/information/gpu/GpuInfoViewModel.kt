/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.features.information.gpu

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.domain.observable.ObservableGpuData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * ViewModel for GPU information. It is using custom SurfaceView to get more GPU details from OpenGL
 *
 * @author kgurgul
 */
class GpuInfoViewModel @ViewModelInject constructor(
        private val observableGpuData: ObservableGpuData
) : ViewModel() {

    private val gpuData: Flow<GpuData> = observableGpuData.observe()

    val viewState = gpuData
            .distinctUntilChanged()
            .map { GpuInfoViewState(it) }
            .asLiveData(viewModelScope.coroutineContext)

    init {
        observableGpuData(ObservableGpuData.Params())
    }

    fun onGlInfoReceived(glVendor: String?, glRenderer: String?, glExtensions: String?) {
        observableGpuData(ObservableGpuData.Params(glVendor, glRenderer, glExtensions))
    }
}