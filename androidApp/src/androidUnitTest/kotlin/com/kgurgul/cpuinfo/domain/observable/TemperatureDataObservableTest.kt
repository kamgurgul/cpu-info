package com.kgurgul.cpuinfo.domain.observable

import android.hardware.SensorManager
import app.cash.turbine.test
import com.kgurgul.cpuinfo.data.provider.TemperatureProvider
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.observe
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_battery
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import com.kgurgul.cpuinfo.utils.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
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
    private val mockSensorManger = mock<SensorManager>()

    private val interactor = TemperatureDataObservable(
        dispatchersProvider = coroutineTestRule.testDispatcherProvider,
        temperatureProvider = mockTemperatureProvider,
        sensorManager = mockSensorManger,
    )

    @Ignore("Reenable after migration to common")
    @Test
    fun `Get temperature data observable`() = runTest {
        val expectedData = listOf(
            TemperatureItem(
                id = -2,
                icon = Res.drawable.ic_cpu_temp,
                name = "CPU",
                temperature = 40f,
            ),
            TemperatureItem(
                id = -1,
                icon = Res.drawable.ic_battery,
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