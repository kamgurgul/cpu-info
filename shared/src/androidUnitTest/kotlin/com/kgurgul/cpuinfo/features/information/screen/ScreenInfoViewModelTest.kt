package com.kgurgul.cpuinfo.features.information.screen

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class ScreenInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val items = TestData.itemRowData
    private val screenDataFlow = flowOf(items)
    private val mockGetScreenDataInteractor = mock<GetScreenDataInteractor> {
        onBlocking { observe(anyOrNull()) } doReturn screenDataFlow
    }

    private val observedUiStates = mutableListOf<ScreenInfoViewModel.UiState>()
    private lateinit var viewModel: ScreenInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            ScreenInfoViewModel.UiState(),
            ScreenInfoViewModel.UiState(
                items = items,
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = ScreenInfoViewModel(
            getScreenDataInteractor = mockGetScreenDataInteractor,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}