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

import com.kgurgul.cpuinfo.common.Prefs
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

/**
 * Tests for [TemperatureFormatter]
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class TemperatureFormatterTest {

    @Test
    fun formatCelsius() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyString()) } doReturn
                    TemperatureFormatter.CELSIUS.toString()
        }
        val formatter = TemperatureFormatter(prefs)

        /* When */
        val temp = formatter.format(9f)

        /* Then */
        assertEquals("9°C", temp)
    }

    @Test
    fun formatFahrenheit() {
        /* Given */
        val prefs = mock<Prefs> {
            onGeneric { get(anyString(), anyString()) } doReturn
                    TemperatureFormatter.FAHRENHEIT.toString()
        }
        val formatter = TemperatureFormatter(prefs)

        /* When */
        val temp = formatter.format(9f)

        /* Then */
        assertEquals("48.2°F", temp)
    }
}