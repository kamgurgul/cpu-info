/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private val interactor =
        ProcessesDataObservable(
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
