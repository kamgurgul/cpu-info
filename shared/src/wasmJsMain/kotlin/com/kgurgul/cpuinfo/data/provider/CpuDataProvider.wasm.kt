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

import kotlinx.browser.window

actual class CpuDataProvider actual constructor() : ICpuDataProvider {

    actual override fun getAbi(): String {
        return "WASM"
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return window.navigator.hardwareConcurrency.toInt()
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return window.navigator.hardwareConcurrency.toInt()
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
