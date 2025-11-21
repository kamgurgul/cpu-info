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

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class FilterApplicationsInteractorTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val interactor =
        FilterApplicationsInteractor(dispatchersProvider = coroutineTestRule.testDispatcherProvider)

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
        val applications =
            listOf(
                ExtendedApplicationData(
                    name = "App1 - ź",
                    packageName = "com.app1",
                    versionName = "1.0.0 - ż",
                    nativeLibs = listOf("/data/app1/lib"),
                    hasNativeLibs = true,
                    appIconUri = "uri",
                ),
                ExtendedApplicationData(
                    name = "App2",
                    packageName = "com.ÓW.test",
                    versionName = "1.0.0",
                    nativeLibs = listOf("/data/app2/lib"),
                    hasNativeLibs = false,
                    appIconUri = "uri",
                ),
                ExtendedApplicationData(
                    name = "App3ów",
                    packageName = "com.app3",
                    versionName = "1.0.0",
                    nativeLibs = listOf("/data/app3/lib"),
                    hasNativeLibs = true,
                    appIconUri = "uri",
                ),
            )

        val nameParams =
            FilterApplicationsInteractor.Params(applications = applications, searchQuery = "app")
        val packageAndNameParams =
            FilterApplicationsInteractor.Params(applications = applications, searchQuery = "óW")
        val versionParams =
            FilterApplicationsInteractor.Params(applications = applications, searchQuery = "   ż  ")

        val nameResult = interactor.invoke(nameParams)
        val packageAndNameResult = interactor.invoke(packageAndNameParams)
        val versionResult = interactor.invoke(versionParams)

        assertEquals(nameResult, applications)
        assertEquals(packageAndNameResult, listOf(applications[1], applications[2]))
        assertEquals(versionResult, listOf(applications[0]))
    }
}
