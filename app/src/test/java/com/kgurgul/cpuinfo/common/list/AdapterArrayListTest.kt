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