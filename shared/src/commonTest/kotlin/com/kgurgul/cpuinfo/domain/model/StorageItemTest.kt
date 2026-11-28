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

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StorageItemTest {
    private fun available(total: Long, used: Long): Long? =
        StorageItem("volume", TextResource.Text("Volume"), Res.drawable.ic_hard_drive, total, used)
            .storageAvailable

    @Test
    fun reportsRemainingBytesForPartialEmptyAndFullVolumes() {
        assertEquals(75L, available(100, 25))
        assertEquals(Long.MAX_VALUE, available(Long.MAX_VALUE, 0))
        assertEquals(0L, available(100, 100))
    }

    @Test
    fun rejectsUnavailableAndInconsistentReadings() {
        assertNull(available(-1, -1))
        assertNull(available(0, 0))
        assertNull(available(100, -1))
        assertNull(available(100, 101))
    }
}
