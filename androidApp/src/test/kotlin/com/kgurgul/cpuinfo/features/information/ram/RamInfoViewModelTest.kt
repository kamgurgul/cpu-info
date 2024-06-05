package com.kgurgul.cpuinfo.features.information.ram

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.RamDataObservable
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

class RamInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val ramData = TestData.ramData
    private val mockRamDataObservable = mock<RamDataObservable> {
        on { observe(anyOrNull()) } doReturn flowOf(ramData)
    }

    private val observedUiStates = mutableListOf<RamInfoViewModel.UiState>()
    private lateinit var viewModel: RamInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            RamInfoViewModel.UiState(),
            RamInfoViewModel.UiState(
                ramData = ramData,
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = RamInfoViewModel(
            ramDataObservable = mockRamDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}