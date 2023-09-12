package com.kgurgul.cpuinfo.domain.observable

import app.cash.turbine.test
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class TemperatureDataObservableTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockTemperatureProvider = mock<TemperatureProvider> {
        on { findCpuTemperatureLocation() } doReturn "/sys/class/thermal/thermal_zone0/temp"
        on { getBatteryTemperature() } doReturn 30f
        on { getCpuTemp(any()) } doReturn 40f
    }

    private val interactor = TemperatureDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        temperatureProvider = mockTemperatureProvider,
    )

    @Test
    fun `Get temperature data observable`() = runTest {
        val expectedData = listOf(
            TemperatureItem(
                iconRes = R.drawable.ic_cpu_temp,
                nameRes = R.string.cpu,
                temperature = 40f
            ),
            TemperatureItem(
                iconRes = R.drawable.ic_battery,
                nameRes = R.string.battery,
                temperature = 30f
            ),
        )

        interactor.observe().test {
            assertEquals(expectedData, awaitItem())
        }
    }
}