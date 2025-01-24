package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeProcessesProvider
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class ProcessesDataObservableTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeProcessesProvider = FakeProcessesProvider()

    private val interactor = ProcessesDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        processesProvider = fakeProcessesProvider,
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
    fun getProcessesDataObservable() = runTest {
        fakeProcessesProvider.processesList = TestData.processes
        val expectedItems = TestData.processes

        interactor.observe(ProcessesDataObservable.Params(SortOrder.ASCENDING)).test {
            assertEquals(expectedItems, awaitItem())
        }
    }
}
