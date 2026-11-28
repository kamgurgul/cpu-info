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

import kotlin.test.Test
import kotlin.test.assertEquals

class FileStoreLabelTest {

    @Test
    fun skipsNameWhenItDuplicatesLabel() {
        assertEquals(
            "Hardware\napfs • /System/Volumes/Hardware",
            fileStoreLabel(
                label = "Hardware",
                name = "Hardware",
                type = "apfs",
                mount = "/System/Volumes/Hardware",
            ),
        )
    }

    @Test
    fun keepsNameWhenItDiffersFromLabel() {
        assertEquals(
            "Windows\nLocal Disk (C:) • NTFS • C:\\",
            fileStoreLabel(
                label = "Windows",
                name = "Local Disk (C:)",
                type = "NTFS",
                mount = "C:\\",
            ),
        )
    }

    @Test
    fun fallsBackToNameAndSkipsBlankOrRepeatedDetails() {
        assertEquals(
            "/\next4",
            fileStoreLabel(label = " ", name = "/", type = "ext4", mount = "/"),
        )
        assertEquals(
            "tmpfs • /run",
            fileStoreLabel(label = null, name = null, type = "tmpfs", mount = "/run"),
        )
    }
}
