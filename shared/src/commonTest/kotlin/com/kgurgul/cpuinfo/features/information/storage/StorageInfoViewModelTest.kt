package com.kgurgul.cpuinfo.features.information.storage

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeStorageDataProvider
import com.kgurgul.cpuinfo.domain.observable.StorageDataObservable
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class StorageInfoViewModelTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val storageData = TestData.storageData
    private val fakeStorageDataProvider = FakeStorageDataProvider()
    private val storageDataObservable = StorageDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        storageDataProvider = fakeStorageDataProvider,
    )

    private lateinit var viewModel: StorageInfoViewModel

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
        viewModel = StorageInfoViewModel(
            storageDataObservable = storageDataObservable,
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun initialUiState() = runTest {
        val expectedUiState = StorageInfoViewModel.UiState(
            storageItems = storageData,
        )

        viewModel.uiStateFlow.test {
            assertEquals(expectedUiState, awaitItem())
        }
    }
}
