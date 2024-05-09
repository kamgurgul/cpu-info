/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.features.temperature

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.data.local.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test

class TemperatureFormatterTest {

    private val userPreferenceSharedFlow = MutableSharedFlow<UserPreferences>(replay = 1)
    private val mockUserPreferencesRepository = mock<IUserPreferencesRepository> {
        on { userPreferencesFlow } doReturn userPreferenceSharedFlow
    }
    private val formatter = TemperatureFormatter(mockUserPreferencesRepository)

    @Test
    fun formatCelsius() = runTest {
        userPreferenceSharedFlow.emit(
            TestData.userPreferences.copy(temperatureUnit = TemperatureFormatter.CELSIUS)
        )

        val temp = formatter.format(9f)

        assertEquals("9°C", temp)
    }

    @Test
    fun formatFahrenheit() = runTest {
        userPreferenceSharedFlow.emit(
            TestData.userPreferences.copy(temperatureUnit = TemperatureFormatter.FAHRENHEIT)
        )

        val temp = formatter.format(9f)

        assertEquals("48.2°F", temp)
    }
}