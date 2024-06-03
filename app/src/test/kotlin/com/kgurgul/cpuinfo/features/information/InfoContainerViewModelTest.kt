package com.kgurgul.cpuinfo.features.information

import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test

class InfoContainerViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockRamCleanupAction = mock<RamCleanupAction>()

    private val observedUiStates = mutableListOf<InfoContainerViewModel.UiState>()
    private lateinit var viewModel: InfoContainerViewModel

    @Test
    fun `On RAM cleanup clicked`() = runTest {
        createViewModel()

        viewModel.onClearRamClicked()

        verify(mockRamCleanupAction).invoke(anyOrNull())
    }

    private fun createViewModel() {
        observedUiStates.clear()
        viewModel = InfoContainerViewModel(
            ramCleanupAction = mockRamCleanupAction,
        ).also {
            it.uiStateFlow
                .onEach(observedUiStates::add)
                .launchIn(CoroutineScope(coroutineTestRule.testDispatcher))
        }
    }
}