package com.kgurgul.cpuinfo.features.processes

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.domain.observable.ProcessesDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.collections.immutable.toImmutableList
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

class ProcessesViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val processes = TestData.processes
    private val mockProcessesDataObservable = mock<ProcessesDataObservable> {
        on { observe(anyOrNull()) } doReturn flowOf(processes)
    }

    private val observedUiStates = mutableListOf<ProcessesViewModel.UiState>()
    private lateinit var viewModel: ProcessesViewModel

    @Test
    fun `Get processes data observable`() {
        val expectedUiStates = listOf(
            ProcessesViewModel.UiState(
                isLoading = false,
                processes = processes.toImmutableList(),
            )
        )

        createViewModel()

        assertEquals(expectedUiStates, observedUiStates)
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = ProcessesViewModel(
            processesDataObservable = mockProcessesDataObservable,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}