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

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu_has_neon
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.yes

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider {

    actual external override fun initLibrary()

    actual external override fun getCpuName(): String

    private external fun hasArmNeon(): Boolean

    actual external override fun getL1dCaches(): IntArray?

    actual external override fun getL1iCaches(): IntArray?

    actual external override fun getL2Caches(): IntArray?

    actual external override fun getL3Caches(): IntArray?

    actual external override fun getL4Caches(): IntArray?

    actual external override fun getNumberOfCores(): Int

    override fun getExtraItems(): List<ItemValue> =
        listOf(
            ItemValue.NameValueResource(
                name = Res.string.cpu_has_neon,
                value = if (hasArmNeon()) Res.string.yes else Res.string.no,
            )
        )
}
