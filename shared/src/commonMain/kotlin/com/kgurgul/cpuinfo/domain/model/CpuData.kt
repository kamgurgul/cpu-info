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
package com.kgurgul.cpuinfo.domain.model

import androidx.compose.runtime.Stable

@Stable
data class CpuData(
    val processorName: String,
    val abi: String,
    val coreNumber: Int,
    val hasArmNeon: Boolean,
    val frequencies: List<Frequency>,
    val l1dCaches: String,
    val l1iCaches: String,
    val l2Caches: String,
    val l3Caches: String,
    val l4Caches: String,
) {

    data class Frequency(val min: Long, val max: Long, val current: Long)
}
