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
package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class FilterProcessesInteractorTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val interactor =
        FilterProcessesInteractor(dispatchersProvider = coroutineTestRule.testDispatcherProvider)

    @BeforeTest
    fun setup() {
        coroutineTestRule.onStart()
    }

    @AfterTest
    fun tearDown() {
        coroutineTestRule.onFinished()
    }

    @Test
    fun filterApplications() = runTest {
        val processes =
            listOf(
                ProcessItem(
                    name = "żćóname",
                    pid = "pid",
                    ppid = "ppid",
                    niceness = "niceness - 0F",
                    user = "user",
                    rss = "rss - ŻĆdd",
                    vsize = "vsize",
                ),
                ProcessItem(
                    name = "name",
                    pid = "pidć",
                    ppid = "ppid",
                    niceness = "niceness",
                    user = "user",
                    rss = "rss",
                    vsize = "vsize ŻĆdd",
                ),
                ProcessItem(
                    name = "name",
                    pid = "pid",
                    ppid = "ppĆid",
                    niceness = "niceness",
                    user = "0fuser",
                    rss = "rss",
                    vsize = "vsize",
                ),
            )

        val namePidPpidParams =
            FilterProcessesInteractor.Params(processes = processes, searchQuery = "Ć")
        val nicenessUserParams =
            FilterProcessesInteractor.Params(processes = processes, searchQuery = "0f")
        val rssParams =
            FilterProcessesInteractor.Params(processes = processes, searchQuery = "  ŻĆdd  ")

        val namePidPpidResult = interactor.invoke(namePidPpidParams)
        val nicenessUserResult = interactor.invoke(nicenessUserParams)
        val rssResult = interactor.invoke(rssParams)

        assertEquals(namePidPpidResult, processes)
        assertEquals(nicenessUserResult, listOf(processes[0], processes[2]))
        assertEquals(rssResult, listOf(processes[0]))
    }
}
