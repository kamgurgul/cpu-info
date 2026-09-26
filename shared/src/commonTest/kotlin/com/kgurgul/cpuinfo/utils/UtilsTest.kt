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
package com.kgurgul.cpuinfo.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun humanReadableByteCountFormatting() {
        /* When */
        val bytes = Utils.humanReadableByteCount(10L)
        val kilo = Utils.humanReadableByteCount(1500L)
        val mega = Utils.humanReadableByteCount(1500L * 1024L)
        val giga = Utils.humanReadableByteCount(1500L * 1024L * 1024L)
        val teta = Utils.humanReadableByteCount(1500L * 1024L * 1024L * 1024L)
        val peta = Utils.humanReadableByteCount(1500L * 1024L * 1024L * 1024L * 1024L)
        val e = Utils.humanReadableByteCount(1500L * 1024L * 1024L * 1024L * 1024L * 1024L)

        /* Then */
        assertEquals("10B", bytes)
        assertEquals("1.46KB", kilo)
        assertEquals("1.46MB", mega)
        assertEquals("1.46GB", giga)
        assertEquals("1.46TB", teta)
        assertEquals("1.46PB", peta)
        assertEquals("1.46EB", e)
    }

    @Test
    fun bytesToMegaFormatting() {
        /* When */
        val bytes = Utils.convertBytesToMega(10L)
        val kilo = Utils.convertBytesToMega(1500L)
        val mega = Utils.convertBytesToMega(1500L * 1024L)
        val giga = Utils.convertBytesToMega(1500L * 1024L * 1024L)

        /* Then */
        assertEquals("0MB", bytes)
        assertEquals("0MB", kilo)
        assertEquals("1.46MB", mega)
        assertEquals("1500MB", giga)
    }

    @Test
    fun uptimeFormatting() {
        /* When */
        val seconds = Utils.formatUptime(42L)
        val minutes = Utils.formatUptime(3L * 60L + 5L)
        val hours = Utils.formatUptime(2L * 3600L + 0L * 60L + 7L)
        val days = Utils.formatUptime(3L * 86400L + 4L * 3600L + 5L * 60L + 6L)

        /* Then */
        assertEquals("42s", seconds)
        assertEquals("3m 5s", minutes)
        assertEquals("2h 0m 7s", hours)
        assertEquals("3d 4h 5m 6s", days)
    }
}
