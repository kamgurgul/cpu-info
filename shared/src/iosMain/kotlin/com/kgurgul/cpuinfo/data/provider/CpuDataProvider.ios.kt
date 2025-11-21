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
package com.kgurgul.cpuinfo.data.provider

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform.cpuArchitecture
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class CpuDataProvider actual constructor() : ICpuDataProvider, KoinComponent {

    private val cpuDataNativeProvider: CpuDataNativeProvider by inject()

    @OptIn(ExperimentalNativeApi::class)
    actual override fun getAbi(): String {
        return cpuArchitecture.name
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return cpuDataNativeProvider.getNumberOfCores()
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return -1L
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return -1L to -1L
    }
}
