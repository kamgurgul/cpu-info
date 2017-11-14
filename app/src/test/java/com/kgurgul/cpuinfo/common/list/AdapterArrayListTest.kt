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

package com.kgurgul.cpuinfo.common.list

import android.databinding.ListChangeRegistry
import com.kgurgul.cpuinfo.utils.Whitebox
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class AdapterArrayListTest {

    @Test
    fun replace() {
        /* Given */
        val listChangeRegistry = mock<ListChangeRegistry>()
        val adapterArrayList = AdapterArrayList<TestData>()
        Whitebox.setInternalState(adapterArrayList, "mListeners", listChangeRegistry)

        /* When */
        val newList = ArrayList<TestData>()
        adapterArrayList.replace(newList)

        /* Then */
        verify(listChangeRegistry, times(1)).notifyChanged(adapterArrayList)
    }

    data class TestData(val name: String)
}