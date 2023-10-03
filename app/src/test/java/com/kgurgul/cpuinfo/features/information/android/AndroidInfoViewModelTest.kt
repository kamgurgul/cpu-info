package com.kgurgul.cpuinfo.features.information.android

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.result.GetOsDataInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class AndroidInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val items = TestData.itemRowData
    private val mockGetOsDataInteractor = mock<GetOsDataInteractor> {
        onBlocking { invoke(anyOrNull()) } doReturn items
    }

    private val observedUiStates = mutableListOf<AndroidInfoViewModel.UiState>()
    private lateinit var viewModel: AndroidInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            AndroidInfoViewModel.UiState(
                items = items,
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = AndroidInfoViewModel(
            getOsDataInteractor = mockGetOsDataInteractor,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}