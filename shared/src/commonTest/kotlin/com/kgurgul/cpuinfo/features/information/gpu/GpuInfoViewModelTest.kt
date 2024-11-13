package com.kgurgul.cpuinfo.features.information.gpu

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeGpuDataProvider
import com.kgurgul.cpuinfo.domain.observable.GpuDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GpuInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val gpuData = TestData.gpuData
    private val fakeGpuDataProvider = FakeGpuDataProvider()
    private val observableGpuData = GpuDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        gpuDataProvider = fakeGpuDataProvider,
    )
    private lateinit var viewModel: GpuInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = GpuInfoViewModel(
            gpuDataObservable = observableGpuData,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = GpuInfoViewModel.UiState(
            gpuData = gpuData,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
