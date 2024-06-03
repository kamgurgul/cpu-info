package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.ProcessesProvider
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessesDataObservableTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockProcessesProvider: ProcessesProvider = mock()

    private val interactor = ProcessesDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        processesProvider = mockProcessesProvider,
    )

    @Test
    fun `Get processes data observable`() = runTest {
        whenever(mockProcessesProvider.getProcessList()).thenReturn(TestData.processes)
        val expectedItems = TestData.processes

        interactor.observe().test {
            assertEquals(expectedItems, awaitItem())
        }
    }
}