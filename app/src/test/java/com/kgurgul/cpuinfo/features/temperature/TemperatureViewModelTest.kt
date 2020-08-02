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

package com.kgurgul.cpuinfo.features.temperature

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kgurgul.cpuinfo.utils.Prefs
import com.kgurgul.cpuinfo.utils.RxImmediateSchedulerRule
import com.nhaarman.mockitokotlin2.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for [TemperatureViewModel]
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class TemperatureViewModelTest {

    @Suppress("unused")
    @get:Rule
    val rxSchedulersRule = RxImmediateSchedulerRule()

    @Suppress("unused")
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Test
    fun startTemperatureRefreshingNoCpuTempSaved() {
        /* Given */
        val cpuTempResult = TemperatureProvider.CpuTemperatureResult()
        val prefs = mock<Prefs> {
            on { contains(anyString()) } doReturn false
        }
        val resources = mock<Resources> {
            on { getString(anyInt()) } doReturn "Test"
        }
        val tempIconProvider = mock<TemperatureIconProvider> {
            on { getIcon(any()) } doReturn 1
        }
        val tempProvider = mock<TemperatureProvider> {
            on { getCpuTemperatureFinder() } doReturn Maybe.just(cpuTempResult)
        }
        val viewModel = spy(TemperatureViewModel(prefs, resources, tempIconProvider, tempProvider))
        doReturn(Observable.just(1L)).whenever(viewModel).getRefreshingInvoker()

        /* When */
        viewModel.startTemperatureRefreshing()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.isError.value)
        assertEquals(1, viewModel.temperatureListLiveData.size)
    }

    @Test
    fun startTemperatureRefreshingCpuTempSaved() {
        /* Given */
        val cpuTempResult = TemperatureProvider.CpuTemperatureResult()
        val prefs = mock<Prefs> {
            on { contains(anyString()) } doReturn true
            onGeneric { get(anyString(), any<TemperatureProvider.CpuTemperatureResult>()) } doReturn
                    cpuTempResult
        }
        val resources = mock<Resources> {
            on { getString(anyInt()) } doReturn "Test"
        }
        val tempIconProvider = mock<TemperatureIconProvider> {
            on { getIcon(any()) } doReturn 1
        }
        val tempProvider = mock<TemperatureProvider>()
        val viewModel = spy(TemperatureViewModel(prefs, resources, tempIconProvider, tempProvider))
        doReturn(Observable.just(1L)).whenever(viewModel).getRefreshingInvoker()

        /* When */
        viewModel.startTemperatureRefreshing()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.isError.value)
        assertEquals(1, viewModel.temperatureListLiveData.size)
    }

    @Test
    fun startTemperatureRefreshingOnlyBatteryTemp() {
        /* Given */
        val prefs = mock<Prefs> {
            on { contains(anyString()) } doReturn false
        }
        val resources = mock<Resources> {
            on { getString(anyInt()) } doReturn "Test"
        }
        val tempIconProvider = mock<TemperatureIconProvider> {
            on { getIcon(any()) } doReturn 1
        }
        val tempProvider = mock<TemperatureProvider> {
            on { getCpuTemperatureFinder() } doReturn
                    Maybe.empty<TemperatureProvider.CpuTemperatureResult>()
            on { getBatteryTemperature() } doReturn 10
        }
        val viewModel = spy(TemperatureViewModel(prefs, resources, tempIconProvider, tempProvider))
        doReturn(Observable.just(1L)).whenever(viewModel).getRefreshingInvoker()

        /* When */
        viewModel.startTemperatureRefreshing()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.isError.value)
        assertEquals(1, viewModel.temperatureListLiveData.size)
    }

    @Test
    fun startTemperatureRefreshingNoTemperatures() {
        /* Given */
        val prefs = mock<Prefs> {
            on { contains(anyString()) } doReturn false
        }
        val resources = mock<Resources>()
        val tempIconProvider = mock<TemperatureIconProvider>()
        val tempProvider = mock<TemperatureProvider> {
            on { getCpuTemperatureFinder() } doReturn
                    Maybe.empty<TemperatureProvider.CpuTemperatureResult>()
        }
        val viewModel = TemperatureViewModel(prefs, resources, tempIconProvider, tempProvider)

        /* When */
        viewModel.startTemperatureRefreshing()

        /* Then */
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.isError.value)
        assertEquals(0, viewModel.temperatureListLiveData.size)
    }
}