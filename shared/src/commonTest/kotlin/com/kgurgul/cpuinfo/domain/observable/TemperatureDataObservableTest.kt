package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.provider.FakeTemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import com.kgurgul.cpuinfo.utils.resources.FakeLocalResources
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest

class TemperatureDataObservableTest {

    val coroutineTestRule = CoroutineTestSuit()

    private val fakeTemperatureProvider = FakeTemperatureProvider(
        sensorsFlow = emptyFlow(),
        batteryTemp = 30f,
        cpuTempLocation = "/sys/class/thermal/thermal_zone0/temp",
        cpuTemp = 40f,
    )
    private val fakeLocalResources = FakeLocalResources()

    private val interactor = TemperatureDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        temperatureProvider = fakeTemperatureProvider,
        localResources = fakeLocalResources,
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
        val expectedData = listOf(
            TemperatureItem(
                id = -2,
                icon = Res.drawable.ic_cpu_temp,
                name = "Test",
                temperature = 40f,
            ),
            TemperatureItem(
                id = -1,
                icon = Res.drawable.ic_battery,
                name = "Test",
                temperature = 30f,
            ),
        )

        interactor.observe().test {
            skipItems(1)
            assertEquals(expectedData, awaitItem())
        }
    }
}
