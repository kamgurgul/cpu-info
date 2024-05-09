package com.kgurgul.cpuinfo.features.information.cpu

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.CpuDataObservable
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

class CpuInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val cpuData = TestData.cpuData
    private val mockCpuDataObservable = mock<CpuDataObservable> {
        on { observe(anyOrNull()) } doReturn flowOf(cpuData)
    }

    private val observedUiStates = mutableListOf<CpuInfoViewModel.UiState>()
    private lateinit var viewModel: CpuInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            CpuInfoViewModel.UiState(),
            CpuInfoViewModel.UiState(
                cpuData = cpuData,
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = CpuInfoViewModel(
            cpuDataObservable = mockCpuDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}