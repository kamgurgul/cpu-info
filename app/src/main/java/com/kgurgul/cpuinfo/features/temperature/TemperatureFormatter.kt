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

import com.kgurgul.cpuinfo.features.settings.SettingsFragment
import com.kgurgul.cpuinfo.utils.Prefs
import com.kgurgul.cpuinfo.utils.round2
import javax.inject.Inject

/**
 * Format temperature using user settings
 *
 * @author kgurgul
 */
class TemperatureFormatter @Inject constructor(val prefs: Prefs) {

    companion object {
        const val CELSIUS = 0
        const val FAHRENHEIT = 1
        const val KELVIN = 2
    }

    /**
     * Format temperature for current settings
     *
     * @param temp formatting temperature which will be formatted (passed in Celsius unit)
     */
    fun format(temp: Float): String {
        val tempUnit = prefs.get(SettingsFragment.KEY_TEMPERATURE_UNIT, CELSIUS.toString())
                .toInt()
        return if (tempUnit == FAHRENHEIT) {
            val fahrenheit = temp * 9 / 5 + 32
            "${fahrenheit.round2()}\u00B0F"
        } else return if (tempUnit == KELVIN){
            val kelvin = temp + 273.15
            "${kelvin.round2()}\u00B0K"
        }
        else {
            val tempFormatted = "${temp.toInt()}\u00B0C"
            tempFormatted
        }
    }
}