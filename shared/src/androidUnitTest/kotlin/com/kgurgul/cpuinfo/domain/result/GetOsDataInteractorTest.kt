package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.OsDataProvider
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class GetOsDataInteractorTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockOsDataProvider: OsDataProvider = mock()

    private val interactor = GetOsDataInteractor(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        osDataProvider = mockOsDataProvider,
    )

    @Test
    fun `Get OS data`() = runTest {
        whenever(mockOsDataProvider.getData()).thenReturn(listOf("TestKey" to "TestValue"))
        val expectedItems = listOf(
            "TestKey" to "TestValue"
        )

        val result = interactor.invoke(Unit)

        assertEquals(expectedItems, result)
    }
}