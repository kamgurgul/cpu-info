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
package com.kgurgul.cpuinfo.data.local

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeUserPreferencesRepository(val preferencesFlow: Flow<UserPreferences> = emptyFlow()) :
    IUserPreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = preferencesFlow

    var isSetApplicationsSortingOrderCalled = false
        private set

    var isSetProcessesSortingOrderCalled = false
        private set

    var isSetApplicationsWithSystemAppsCalled = false
        private set

    var isSetTemperatureUnitCalled = false
        private set

    var isSetThemeCalled = false
        private set

    override suspend fun setApplicationsSortingOrder(isAscending: Boolean) {
        isSetApplicationsSortingOrderCalled = true
    }

    override suspend fun setProcessesSortingOrder(isAscending: Boolean) {
        isSetProcessesSortingOrderCalled = true
    }

    override suspend fun setApplicationsWithSystemApps(withSystemApps: Boolean) {
        isSetApplicationsWithSystemAppsCalled = true
    }

    override suspend fun setTemperatureUnit(temperatureUnit: Int) {
        isSetTemperatureUnitCalled = true
    }

    override suspend fun setTheme(theme: String) {
        isSetThemeCalled = true
    }

    fun reset() {
        isSetApplicationsSortingOrderCalled = false
        isSetProcessesSortingOrderCalled = false
        isSetApplicationsWithSystemAppsCalled = false
        isSetTemperatureUnitCalled = false
        isSetThemeCalled = false
    }
}
