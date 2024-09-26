package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeProcessesProvider
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessesDataObservableTest {

    val coroutineTestRule = CoroutineTestSuit()

    private val fakeProcessesProvider = FakeProcessesProvider()

    private val interactor = ProcessesDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        processesProvider = fakeProcessesProvider,
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
    fun `Get processes data observable`() = runTest {
        fakeProcessesProvider.processesList = TestData.processes
        val expectedItems = TestData.processes

        interactor.observe(ProcessesDataObservable.Params(SortOrder.ASCENDING)).test {
            assertEquals(expectedItems, awaitItem())
        }
    }
}