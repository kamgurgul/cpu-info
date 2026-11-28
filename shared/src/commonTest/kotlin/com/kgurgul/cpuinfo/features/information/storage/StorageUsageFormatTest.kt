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
package com.kgurgul.cpuinfo.features.information.storage

import kotlin.test.Test
import kotlin.test.assertEquals

class StorageUsageFormatTest {

    @Test
    fun roundsRegularUsage() {
        assertEquals("25%", formatUsedPercent(used = 25, total = 100))
        assertEquals("50%", formatUsedPercent(used = 504, total = 1_000))
        assertEquals("51%", formatUsedPercent(used = 505, total = 1_000))
        assertEquals("99%", formatUsedPercent(used = 990, total = 1_000))
    }

    @Test
    fun reportsExactlyEmptyAndFullVolumes() {
        assertEquals("0%", formatUsedPercent(used = 0, total = 100))
        assertEquals("100%", formatUsedPercent(used = 100, total = 100))
    }

    @Test
    fun doesNotRoundNearlyEmptyOrFullVolumesToTheEdges() {
        assertEquals("<1%", formatUsedPercent(used = 4, total = 1_000))
        assertEquals("<1%", formatUsedPercent(used = 6, total = 1_000))
        assertEquals(">99%", formatUsedPercent(used = 996, total = 1_000))
        assertEquals(">99%", formatUsedPercent(used = Long.MAX_VALUE - 1, total = Long.MAX_VALUE))
    }
}
