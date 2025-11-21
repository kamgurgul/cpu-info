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
import com.kgurgul.cpuinfo.data.provider.FakeTemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.battery
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest

class TemperatureDataObservableTest {

    private val coroutineTestRule = CoroutineTestSuit()

    private val fakeTemperatureProvider =
        FakeTemperatureProvider(
            sensorsFlow = emptyFlow(),
            batteryTemp = 30f,
            cpuTempLocation = "/sys/class/thermal/thermal_zone0/temp",
            cpuTemp = 40f,
        )

    private val interactor =
        TemperatureDataObservable(
            dispatchersProvider = coroutineTestRule.testDispatcherProvider,
            temperatureProvider = fakeTemperatureProvider,
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
    fun getTemperatureDataObservable() = runTest {
        val expectedData =
            listOf(
                TemperatureItem(
                    id = -2,
                    icon = Res.drawable.ic_cpu_temp,
                    name = TextResource.Resource(Res.string.cpu),
                    temperature = 40f,
                ),
                TemperatureItem(
                    id = -1,
                    icon = Res.drawable.ic_battery,
                    name = TextResource.Resource(Res.string.battery),
                    temperature = 30f,
                ),
            )

        interactor.observe().test {
            skipItems(1)
            assertEquals(expectedData, awaitItem())
        }
    }
}
