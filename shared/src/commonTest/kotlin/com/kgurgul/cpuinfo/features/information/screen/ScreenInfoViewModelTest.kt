package com.kgurgul.cpuinfo.features.information.screen

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeScreenDataProvider
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import com.kgurgul.cpuinfo.utils.resources.FakeLocalResources
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest

class ScreenInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val items = TestData.itemRowData
    private val fakeScreenDataProvider = FakeScreenDataProvider(
        data = items,
    )
    private val fakeLocalResources = FakeLocalResources(
        stringValue = "Orientation",
    )
    private val getScreenDataInteractor = GetScreenDataInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        localResources = fakeLocalResources,
        screenDataProvider = fakeScreenDataProvider,
    )

    private lateinit var viewModel: ScreenInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = ScreenInfoViewModel(
            getScreenDataInteractor = getScreenDataInteractor,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = ScreenInfoViewModel.UiState(
            items = persistentListOf(
                "test" to "",
                "test" to "test",
                "Orientation" to "Unknown",
            ),
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
