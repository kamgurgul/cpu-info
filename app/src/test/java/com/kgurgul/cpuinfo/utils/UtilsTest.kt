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

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for [Utils] class
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun addPairIfExists() {
        /* Given */
        val testList = ArrayList<Pair<String, String>>()

        /* When */
        Utils.addPairIfExists(testList, "Key", null)
        Utils.addPairIfExists(testList, "Key", "Value")

        /* Then */
        assertEquals(1, testList.size)
        assertEquals("Key", testList[0].first)
        assertEquals("Value", testList[0].second)
    }

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
        assertEquals("10 B", bytes)
        assertEquals("1.46 KB", kilo)
        assertEquals("1.46 MB", mega)
        assertEquals("1.46 GB", giga)
        assertEquals("1.46 TB", teta)
        assertEquals("1.46 PB", peta)
        assertEquals("1.46 EB", e)
    }

    @Test
    fun bytesToMegaFormatting() {
        /* When */
        val bytes = Utils.convertBytesToMega(10L)
        val kilo = Utils.convertBytesToMega(1500L)
        val mega = Utils.convertBytesToMega(1500L * 1024L)
        val giga = Utils.convertBytesToMega(1500L * 1024L * 1024L)

        /* Then */
        assertEquals("0 MB", bytes)
        assertEquals("0 MB", kilo)
        assertEquals("1.46 MB", mega)
        assertEquals("1500 MB", giga)
    }
}