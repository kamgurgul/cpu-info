package com.kgurgul.cpuinfo.domain.result

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
        coroutineTestRule.onStar()
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun `Get OS data`() = runTest {
        val expectedItems = listOf(
            "OS" to "Test OS",
        )

        val result = interactor.invoke(Unit)

        assertEquals(expectedItems, result)
    }
}
