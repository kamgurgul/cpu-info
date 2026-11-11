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
package com.kgurgul.cpuinfo.navigation

import com.kgurgul.cpuinfo.features.applications.ApplicationsRoute
import com.kgurgul.cpuinfo.features.findHostDeepLinkRoute
import com.kgurgul.cpuinfo.features.information.InformationRoute
import com.kgurgul.cpuinfo.features.processes.ProcessesRoute
import com.kgurgul.cpuinfo.features.settings.LicensesRoute
import com.kgurgul.cpuinfo.features.settings.SettingsRoute
import com.kgurgul.cpuinfo.features.temperature.TemperaturesRoute
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HostDeepLinksTest {

    @Test
    fun supportedDeepLinksAreMatched() {
        val base = NavigationConst.BASE_URL
        assertEquals(InformationRoute, findHostDeepLinkRoute(base + "information"))
        assertEquals(ApplicationsRoute, findHostDeepLinkRoute(base + "applications"))
        assertEquals(ProcessesRoute, findHostDeepLinkRoute(base + "processes"))
        assertEquals(TemperaturesRoute, findHostDeepLinkRoute(base + "temperatures"))
        assertEquals(SettingsRoute, findHostDeepLinkRoute(base + "settings"))
        assertEquals(LicensesRoute, findHostDeepLinkRoute(base + "settings/licenses"))
    }

    @Test
    fun unsupportedDeepLinksAreIgnored() {
        assertNull(findHostDeepLinkRoute(null))
        assertNull(findHostDeepLinkRoute(""))
        assertNull(findHostDeepLinkRoute(NavigationConst.BASE_URL))
        assertNull(findHostDeepLinkRoute(NavigationConst.BASE_URL + "unknown"))
        assertNull(findHostDeepLinkRoute("https://example.com/assets/cpuinfo/settings"))
    }
}
