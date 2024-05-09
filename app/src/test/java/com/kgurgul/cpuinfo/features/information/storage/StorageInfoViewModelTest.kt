package com.kgurgul.cpuinfo.features.information.storage

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.StorageDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class StorageInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val storageData = TestData.storageData
    private val mockStorageDataObservable = mock<StorageDataObservable> {
        on { observe() } doReturn flowOf(storageData)
    }

    private val observedUiStates = mutableListOf<StorageInfoViewModel.UiState>()
    private lateinit var viewModel: StorageInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            StorageInfoViewModel.UiState(),
            StorageInfoViewModel.UiState(
                storageItems = storageData,
            ),
        )

        createViewModel()
        viewModel.onRefreshStorage()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = StorageInfoViewModel(
            storageDataObservable = mockStorageDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}