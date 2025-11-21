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
package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.removeNonSpacingMarks
import kotlinx.coroutines.CoroutineDispatcher

class FilterApplicationsInteractor(private val dispatchersProvider: IDispatchersProvider) :
    ResultInteractor<FilterApplicationsInteractor.Params, List<ExtendedApplicationData>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.default

    override suspend fun doWork(params: Params): List<ExtendedApplicationData> {
        val normalizedQuery = params.searchQuery.trim().lowercase().removeNonSpacingMarks()
        return params.applications.filter {
            it.name.lowercase().removeNonSpacingMarks().contains(normalizedQuery) ||
                it.packageName.lowercase().removeNonSpacingMarks().contains(normalizedQuery) ||
                it.versionName.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
        }
    }

    data class Params(val applications: List<ExtendedApplicationData>, val searchQuery: String)
}
