package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeOsDataProvider
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetOsDataInteractorTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeOsDataProvider = FakeOsDataProvider()

    private val interactor = GetOsDataInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        osDataProvider = fakeOsDataProvider,
    )

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun getOSDataObservable() = runTest {
        val expectedItems = TestData.itemRowData

        interactor.observe(Unit).test {
            assertEquals(expectedItems, awaitItem())
        }
    }
}
