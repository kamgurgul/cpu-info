package com.kgurgul.cpuinfo.features.information.cpu

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeCpuDataNativeProvider
import com.kgurgul.cpuinfo.data.provider.FakeCpuDataProvider
import com.kgurgul.cpuinfo.domain.observable.CpuDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class CpuInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val cpuData = TestData.cpuData
    private val fakeCpuDataProvider = FakeCpuDataProvider()
    private val fakeCpuDataNativeProvider = FakeCpuDataNativeProvider()
    private val cpuDataObservable = CpuDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        cpuDataProvider = fakeCpuDataProvider,
        cpuDataNativeProvider = fakeCpuDataNativeProvider,
    )

    private lateinit var viewModel: CpuInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = CpuInfoViewModel(
            cpuDataObservable = cpuDataObservable,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = CpuInfoViewModel.UiState(
            cpuData = cpuData,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
