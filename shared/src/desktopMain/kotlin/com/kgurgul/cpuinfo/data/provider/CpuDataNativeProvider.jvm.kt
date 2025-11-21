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

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual override fun initLibrary() {}

    actual override fun getCpuName(): String {
        return processor.processorIdentifier.name
    }

    actual override fun hasArmNeon(): Boolean {
        return processor.featureFlags.contains("neon")
    }

    actual override fun getL1dCaches(): IntArray? {
        return null
    }

    actual override fun getL1iCaches(): IntArray? {
        return null
    }

    actual override fun getL2Caches(): IntArray? {
        return null
    }

    actual override fun getL3Caches(): IntArray? {
        return null
    }

    actual override fun getL4Caches(): IntArray? {
        return null
    }

    actual override fun getNumberOfCores(): Int {
        return processor.physicalProcessorCount
    }
}
