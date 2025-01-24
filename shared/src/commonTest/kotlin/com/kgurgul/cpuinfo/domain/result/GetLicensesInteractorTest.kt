package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.provider.FakeLicensesProvider
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class GetLicensesInteractorTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeLicensesProvider = FakeLicensesProvider()

    private val interactor = GetLicensesInteractor(
        licensesProvider = fakeLicensesProvider,
        dispatchersProvider = coroutineTestRule.testDispatcherProvider
    )

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
        fakeLicensesProvider.reset()
    }

    @Test
    fun getLicensesSuccess() = runTest {
        val expectedData = TestData.licenses

        val result = interactor.invoke(Unit).getOrThrow()

        assertEquals(expectedData, result)
    }

    @Test
    fun getLicensesError() = runTest {
        fakeLicensesProvider.throwError = true

        val result = interactor.invoke(Unit)

        assertTrue(result.isFailure)
    }
}
