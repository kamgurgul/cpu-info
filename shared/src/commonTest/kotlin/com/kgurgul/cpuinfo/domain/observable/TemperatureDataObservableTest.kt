package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.provider.FakeTemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.CoroutineTestSuit
import com.kgurgul.cpuinfo.utils.resources.ILocalResources
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.StringResource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureDataObservableTest {

    val coroutineTestRule = CoroutineTestSuit()

    private val fakeTemperatureProvider = FakeTemperatureProvider(
        sensorsFlow = emptyFlow(),
        batteryTemp = 30f,
        cpuTempLocation = "/sys/class/thermal/thermal_zone0/temp",
        cpuTemp = 40f,
    )
    private val stubLocalResources = object : ILocalResources {
        override suspend fun getString(resource: StringResource): String = "Test"
    }

    private val interactor = TemperatureDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        temperatureProvider = fakeTemperatureProvider,
        localResources = stubLocalResources,
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
    fun `Get temperature data observable`() = runTest {
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
