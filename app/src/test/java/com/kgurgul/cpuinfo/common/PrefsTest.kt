package com.kgurgul.cpuinfo.common

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for [Prefs] wrapper
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class PrefsTest {

    @Test
    fun contains() {
        /* Given */
        val sharedPreferences = mock<SharedPreferences>()
        whenever(sharedPreferences.contains(anyString()))
                .thenReturn(true)
                .thenReturn(false)
        val prefs = Prefs(sharedPreferences)

        /* When */
        val t1 = prefs.contains("Test1")
        val t2 = prefs.contains("Test2")

        /* Then */
        assertTrue(t1)
        assertFalse(t2)
    }

    @Test
    fun insert() {
        /* Given */
        val editor = mock<SharedPreferences.Editor> { editor ->
            on { putInt(any(), any()) } doReturn editor
            on { putFloat(any(), any()) } doReturn editor
            on { putString(any(), any()) } doReturn editor
            on { putBoolean(any(), any()) } doReturn editor
            on { putLong(any(), any()) } doReturn editor
        }
        val sharedPreferences = mock<SharedPreferences> {
            on { edit() } doReturn editor
        }
        val order = Mockito.inOrder(editor)
        val prefs = Prefs(sharedPreferences)

        /* When */
        prefs.insert("Int", 1)
        prefs.insert("Float", 1f)
        prefs.insert("String", "Test")
        prefs.insert("Boolean", true)
        prefs.insert("Long", 1L)
        prefs.insert("TestData", TestData("test"))

        /* Then */
        order.verify(editor).putInt(any(), any())
        order.verify(editor).putFloat(any(), any())
        order.verify(editor).putString(any(), any())
        order.verify(editor).putBoolean(any(), any())
        order.verify(editor).putLong(any(), any())
        order.verify(editor).putString(any(), any())
    }

    @Test
    fun get() {
        /* Given */
        val sharedPreferences = mock<SharedPreferences> {
            on { getString(eq("TestData"), any()) } doReturn "{\"name\": \"test\"}"
        }
        val order = Mockito.inOrder(sharedPreferences)
        val prefs = Prefs(sharedPreferences)

        /* When */
        prefs.get("Int", 1)
        prefs.get("Float", 1f)
        prefs.get("String", "Test")
        prefs.get("Boolean", true)
        prefs.get("Long", 1L)
        prefs.get("TestData", TestData("test"))

        /* Then */
        order.verify(sharedPreferences).getInt(any(), any())
        order.verify(sharedPreferences).getFloat(any(), any())
        order.verify(sharedPreferences).getString(any(), any())
        order.verify(sharedPreferences).getBoolean(any(), any())
        order.verify(sharedPreferences).getLong(any(), any())
        order.verify(sharedPreferences).getString(any(), any())
    }

    @Test
    fun remove() {
        /* Given */
        val editor = mock<SharedPreferences.Editor> { editor ->
            on { remove(any()) } doReturn editor
        }
        val sharedPreferences = mock<SharedPreferences> {
            on { edit() } doReturn editor
        }
        val prefs = Prefs(sharedPreferences)

        /* When */
        prefs.remove("test")

        /* Then */
        verify(editor).remove("test")
    }

    data class TestData(val name: String)
}