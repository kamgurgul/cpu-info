package com.kgurgul.cpuinfo.features.information.screen

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeScreenDataProvider
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.result.GetScreenDataInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.orientation
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest

class ScreenInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val items = TestData.itemValueRowData
    private val fakeScreenDataProvider = FakeScreenDataProvider(
        data = items,
    )
    private val getScreenDataInteractor = GetScreenDataInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
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
                ItemValue.Text("test", ""),
                ItemValue.Text("test", "test"),
                ItemValue.NameResource(Res.string.orientation, "Unknown"),
            ),
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
