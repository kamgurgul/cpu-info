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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData

class FakeApplicationsDataProvider(
    var applicationsSupported: Boolean = false,
    var installedApplications: List<ExtendedApplicationData> = emptyList(),
    var hasSystemAppsFiltering: Boolean = false,
    var hasAppManagementSupported: Boolean = false,
    var hasManualRefresh: Boolean = false,
) : IApplicationsDataProvider {

    override fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData> {
        return installedApplications
    }

    override fun areApplicationsSupported(): Boolean {
        return applicationsSupported
    }

    override fun hasSystemAppsFiltering(): Boolean {
        return hasSystemAppsFiltering
    }

    override fun hasExpandedAppManagementSupported(): Boolean {
        return hasAppManagementSupported
    }

    override fun hasManualRefresh(): Boolean {
        return hasManualRefresh
    }
}
