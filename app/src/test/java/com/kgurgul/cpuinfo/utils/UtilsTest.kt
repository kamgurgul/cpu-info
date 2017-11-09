package com.kgurgul.cpuinfo.utils

import junit.framework.Assert.assertEquals
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
        val bytes = Utils.humanReadableByteCount(10)
        val kilo = Utils.humanReadableByteCount(1500)
        val mega = Utils.humanReadableByteCount(1500 * 1024)
        val giga = Utils.humanReadableByteCount(1500 * 1024 * 1024)
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
        val bytes = Utils.convertBytesToMega(10)
        val kilo = Utils.convertBytesToMega(1500)
        val mega = Utils.convertBytesToMega(1500 * 1024)
        val giga = Utils.convertBytesToMega(1500 * 1024 * 1024)

        /* Then */
        assertEquals("0 MB", bytes)
        assertEquals("0 MB", kilo)
        assertEquals("1.46 MB", mega)
        assertEquals("1500 MB", giga)
    }
}