package com.kgurgul.cpuinfo.features

import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class HostViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockProcessesDataObservable = mock<ProcessesDataObservable> {
        on { areProcessesSupported() } doReturn true
    }

    private val observedUiStates = mutableListOf<HostViewModel.UiState>()
    private lateinit var viewModel: HostViewModel

    @Test
    fun `Create VM`() {
        val expectedUiStates = listOf(
            HostViewModel.UiState(
                isProcessSectionVisible = true,
            )
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = HostViewModel(
            processesDataObservable = mockProcessesDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}