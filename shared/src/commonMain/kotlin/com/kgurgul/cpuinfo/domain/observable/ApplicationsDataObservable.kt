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
package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.IApplicationsDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.wrapToResultFlow
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.coroutines.flow.Flow

class ApplicationsDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val applicationsDataProvider: IApplicationsDataProvider,
) : MutableInteractor<ApplicationsDataObservable.Params, Result<List<ExtendedApplicationData>>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params): Flow<Result<List<ExtendedApplicationData>>> {
        return wrapToResultFlow {
            val apps = applicationsDataProvider.getInstalledApplications(params.withSystemApps)
            when (params.sortOrder) {
                SortOrder.ASCENDING -> apps.sorted()
                SortOrder.DESCENDING -> apps.sortedDescending()
                else -> apps
            }
        }
    }

    fun areApplicationsSupported() = applicationsDataProvider.areApplicationsSupported()

    data class Params(val withSystemApps: Boolean, val sortOrder: SortOrder = SortOrder.NONE)
}
