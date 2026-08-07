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

import com.kgurgul.cpuinfo.utils.CpuLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class CpuDataProvider actual constructor() : ICpuDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val processor = systemInfo.hardware.processor

    actual override fun getAbi(): String {
        return System.getProperty("os.arch")
    }

    actual override fun getNumberOfLogicalCores(): Int {
        return processor.logicalProcessorCount
    }

    actual override fun getNumberOfPhysicalCores(): Int {
        return processor.physicalProcessorCount
    }

    actual override fun getCurrentFreq(coreNumber: Int): Long {
        return try {
            val max = processor.maxFreq
            val cpuLoad = processor.getProcessorCpuLoad(100)[coreNumber]
            (max * cpuLoad).toLong()
        } catch (e: Exception) {
            CpuLogger.e { "Error getting current frequency for core $coreNumber: $e" }
            -1
        }
    }

    actual override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(0, processor.maxFreq)
    }
}
