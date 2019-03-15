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

package com.kgurgul.cpuinfo.features.applications

import android.content.pm.PackageManager
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.Prefs
import com.kgurgul.cpuinfo.utils.RxImmediateSchedulerRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for [ApplicationsViewModel]
 *
 * @author kgurgul
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ApplicationsViewModelTest {

    @Suppress("unused")
    @get:Rule
    val rxSchedulersRule = RxImmediateSchedulerRule()

    @Suppress("unused")
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    private lateinit var dispatchersProvider: DispatchersProvider

    @Before
    fun setup() {
        dispatchersProvider = mock {
            on { ioDispatcher } doReturn Dispatchers.Main
            on { mainDispatcher } doReturn Dispatchers.Main
        }
    }

    @Test
    fun refreshApplicationsListSuccess() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(dispatchersProvider, prefs, packageManager))
        doReturn(Single.just(getDummyAppInfo(3))).whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertEquals(3, viewModel.applicationList.size)
    }

    @Test
    fun refreshApplicationsListError() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(dispatchersProvider, prefs, packageManager))
        doReturn(Single.error<NullPointerException>(NullPointerException()))
                .whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertEquals(0, viewModel.applicationList.size)
    }

    @Test
    fun sortingAscAndDesc() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(dispatchersProvider, prefs, packageManager))
        doReturn(Single.just(getDummyAppInfo(3))).whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()
        val sortedListAsc = viewModel.getAppSortedList(true)
        val sortedListDesc = viewModel.getAppSortedList(false)

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertEquals(3, viewModel.applicationList.size)
        assertEquals(3, sortedListAsc.size)
        assertEquals(3, sortedListDesc.size)
        assertEquals("test0", sortedListAsc[0].name)
        assertEquals("test2", sortedListAsc[2].name)
        assertEquals("test2", sortedListDesc[0].name)
        assertEquals("test0", sortedListDesc[2].name)
    }

    private fun getDummyAppInfo(amount: Int): List<ExtendedAppInfo> {
        val list = ArrayList<ExtendedAppInfo>()
        (0 until amount).mapTo(list) {
            ExtendedAppInfo("test$it", "test.$it",
                    "test,$it", null, false, Uri.EMPTY, 0)
        }
        return list
    }
}