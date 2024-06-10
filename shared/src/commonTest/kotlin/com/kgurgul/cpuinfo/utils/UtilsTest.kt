/*
 * Copyright 2017 KG Soft
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
}