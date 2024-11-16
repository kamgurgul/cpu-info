package com.kgurgul.cpuinfo.features.information

import com.kgurgul.cpuinfo.components.FakeRamCleanupComponent
import com.kgurgul.cpuinfo.domain.action.RamCleanupAction
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class InfoContainerViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeRamCleanupComponent = FakeRamCleanupComponent()
    private val ramCleanupAction = RamCleanupAction(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        ramCleanupComponent = fakeRamCleanupComponent,
    )

    private lateinit var viewModel: InfoContainerViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        fakeRamCleanupComponent.reset()
        viewModel = InfoContainerViewModel(
            ramCleanupAction = ramCleanupAction,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun onClearRamClicked() = runTest {
        viewModel.onClearRamClicked()

        assertTrue(fakeRamCleanupComponent.cleanupInvoked)
    }
}
