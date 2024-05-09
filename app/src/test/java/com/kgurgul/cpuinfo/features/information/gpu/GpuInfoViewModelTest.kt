package com.kgurgul.cpuinfo.features.information.gpu

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.GpuDataObservable
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

class GpuInfoViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val gpuData = TestData.gpuData
    private val mockGpuDataObservable = mock<GpuDataObservable> {
        on { observe() } doReturn flowOf(gpuData)
    }

    private val observedUiStates = mutableListOf<GpuInfoViewModel.UiState>()
    private lateinit var viewModel: GpuInfoViewModel

    @Test
    fun `Get CPU data observable`() {
        val expectedUiStates = listOf(
            GpuInfoViewModel.UiState(),
            GpuInfoViewModel.UiState(
                gpuData = gpuData,
            ),
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = GpuInfoViewModel(
            observableGpuData = mockGpuDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}