package com.kgurgul.cpuinfo.features.applications

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.pm.PackageManager
import com.kgurgul.cpuinfo.common.Prefs
import com.kgurgul.cpuinfo.utils.RxImmediateSchedulerRule
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for [ApplicationsViewModel]
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class ApplicationsViewModelTest {

    companion object {
        @ClassRule
        @JvmField
        val rxSchedulersRule = RxImmediateSchedulerRule()

        @ClassRule
        @JvmField
        val liveDataRule = InstantTaskExecutorRule()
    }

    @Test
    fun refreshApplicationsListSuccess() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(prefs, packageManager))
        doReturn(Single.just(getDummyAppInfo(3))).whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()

        /* Then */
        assertFalse(viewModel.isLoading.get())
        assertEquals(3, viewModel.applicationList.size)
    }

    @Test
    fun refreshApplicationsListError() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(prefs, packageManager))
        doReturn(Single.error<NullPointerException>(NullPointerException()))
                .whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()

        /* Then */
        assertFalse(viewModel.isLoading.get())
        assertEquals(0, viewModel.applicationList.size)
    }

    @Test
    fun sortingAscAndDesc() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyBoolean()) } doReturn true
        }
        val packageManager = mock<PackageManager>()
        val viewModel = spy(ApplicationsViewModel(prefs, packageManager))
        doReturn(Single.just(getDummyAppInfo(3))).whenever(viewModel).getApplicationsListSingle()

        /* When */
        viewModel.refreshApplicationsList()
        val sortedListAsc = viewModel.getAppSortedList(true)
        val sortedListDesc = viewModel.getAppSortedList(false)

        /* Then */
        assertFalse(viewModel.isLoading.get())
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
        (0 until amount).mapTo(list) { ExtendedAppInfo("test$it", "test.$it", null, 0) }
        return list
    }
}