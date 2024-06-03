package com.kgurgul.cpuinfo.domain.observable

import android.content.Context
import android.hardware.SensorManager
import app.cash.turbine.test
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureDataObservableTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val mockTemperatureProvider = mock<TemperatureProvider> {
        on { findCpuTemperatureLocation() } doReturn "/sys/class/thermal/thermal_zone0/temp"
        on { getBatteryTemperature() } doReturn 30f
        on { getCpuTemp(any()) } doReturn 40f
    }
    private val mockContext = mock<Context> {
        on { getString(R.string.battery) } doReturn "Battery"
        on { getString(R.string.cpu) } doReturn "CPU"
    }
    private val mockSensorManger = mock<SensorManager>()

    private val interactor = TemperatureDataObservable(
        context = mockContext,
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        temperatureProvider = mockTemperatureProvider,
        sensorManager = mockSensorManger,
    )

    @Test
    fun `Get temperature data observable`() = runTest {
        val expectedData = listOf(
            TemperatureItem(
                id = -2,
                iconRes = R.drawable.ic_cpu_temp,
                name = "CPU",
                temperature = 40f,
            ),
            TemperatureItem(
                id = -1,
                iconRes = R.drawable.ic_battery,
                name = "Battery",
                temperature = 30f,
            ),
        )

        interactor.observe().test {
            skipItems(1)
            assertEquals(expectedData, awaitItem())
        }
    }
}