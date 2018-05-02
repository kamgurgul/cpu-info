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

package com.kgurgul.cpuinfo.utils.lifecycleawarelist

import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class ListLiveDataTest {

    @Suppress("unused")
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun add() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_INSERTED,
                0, 1)

        /* When */
        listLiveData.add(TestData("new1"))

        /* Then */
        assertEquals(1, listLiveData.size)
        assertEquals("new1", listLiveData[0].name)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun addWithIndex() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old"))
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_INSERTED,
                0, 1)

        /* When */
        listLiveData.add(0, TestData("new1"))

        /* Then */
        assertEquals(2, listLiveData.size)
        assertEquals("new1", listLiveData[0].name)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun addAll() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_INSERTED,
                0, 2)

        /* When */
        val newList = ArrayList<TestData>()
        newList.add(TestData("new1"))
        newList.add(TestData("new2"))
        listLiveData.addAll(newList)

        /* Then */
        assertEquals(2, listLiveData.size)
        assertEquals("new1", listLiveData[0].name)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun addAllWithIndex() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old"))
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_INSERTED,
                0, 2)

        /* When */
        val newList = ArrayList<TestData>()
        newList.add(TestData("new1"))
        newList.add(TestData("new2"))
        listLiveData.addAll(0, newList)

        /* Then */
        assertEquals(3, listLiveData.size)
        assertEquals("new1", listLiveData[0].name)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun clear() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old1"))
        listLiveData.add(TestData("old2"))
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_REMOVED,
                0, 2)

        /* When */
        listLiveData.clear()

        /* Then */
        assertEquals(0, listLiveData.size)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun removeAt() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old1"))
        listLiveData.add(TestData("old2"))
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_REMOVED,
                0, 1)

        /* When */
        listLiveData.removeAt(0)

        /* Then */
        assertEquals(1, listLiveData.size)
        assertEquals("old2", listLiveData[0].name)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun remove() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        val itemToRemove = TestData("old1")
        val itemNotAdded = TestData("old2")
        listLiveData.add(itemToRemove)
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_REMOVED,
                0, 1)

        /* When */
        val removeResult1 = listLiveData.remove(itemToRemove)
        val removeResult2 = listLiveData.remove(itemNotAdded)

        /* Then */
        assertEquals(0, listLiveData.size)
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
        assertTrue(removeResult1)
        assertFalse(removeResult2)
    }

    @Test
    fun set() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old1"))
        listLiveData.add(TestData("old2"))
        val itemToSet = TestData("new1")
        val expectedEvent = ListLiveDataChangeEvent(ListLiveDataState.ITEM_RANGE_CHANGED,
                1, 1)

        /* When */
        listLiveData[1] = itemToSet

        /* Then */
        assertEquals(2, listLiveData.size)
        assertEquals(itemToSet, listLiveData[1])
        assertEquals(expectedEvent, listLiveData.listStatusChangeNotificator.value!!)
    }

    @Test
    fun replace() {
        /* Given */
        val listLiveData = ListLiveData<TestData>()
        listLiveData.add(TestData("old"))

        /* When */
        val newList = ArrayList<TestData>()
        newList.add(TestData("new1"))
        newList.add(TestData("new2"))
        listLiveData.replace(newList)

        /* Then */
        assertEquals(2, listLiveData.size)
        assertEquals("new1", listLiveData[0].name)
        assertEquals(ListLiveDataState.CHANGED,
                listLiveData.listStatusChangeNotificator.value!!.listLiveDataState)
    }

    data class TestData(val name: String)
}