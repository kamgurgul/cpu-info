package com.kgurgul.cpuinfo.features.information.os

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeOsDataProvider
import com.kgurgul.cpuinfo.domain.observable.GetOsDataInteractor
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class OsInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val items = TestData.itemValueRowData
    private val fakeOsDataProvider = FakeOsDataProvider()
    private val getOsDataInteractor = GetOsDataInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        osDataProvider = fakeOsDataProvider,
    )

    private lateinit var viewModel: OsInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = OsInfoViewModel(
            getOsDataInteractor = getOsDataInteractor,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = OsInfoViewModel.UiState(
            items = items,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
