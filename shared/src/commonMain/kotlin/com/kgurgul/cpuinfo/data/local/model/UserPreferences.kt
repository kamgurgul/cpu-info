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
package com.kgurgul.cpuinfo.data.local.model

data class UserPreferences(
    val isApplicationsSortingAscending: Boolean,
    val isProcessesSortingAscending: Boolean,
    val withSystemApps: Boolean,
    val temperatureUnit: Int,
    val theme: String,
) {

    companion object {
        const val KEY_SORTING_APPS = "sorting_apps"
        const val KEY_SORTING_PROCESSES = "sorting_processes"
        const val KEY_WITH_SYSTEM_APPS = "with_system_apps"
        const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        const val KEY_THEME = "theme"
    }
}
