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

import com.kgurgul.cpuinfo.data.local.model.LicenseData
import com.kgurgul.cpuinfo.data.provider.ILicensesProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher

class GetLicensesInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val licensesProvider: ILicensesProvider,
) : ResultInteractor<Unit, Result<List<License>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override suspend fun doWork(params: Unit): Result<List<License>> {
        return runCatching { licensesProvider.getAll().map { it.toDomain() } }
    }

    private fun LicenseData.toDomain() =
        License(
            moduleName = moduleName,
            moduleVersion = moduleVersion,
            license = moduleLicense,
            licenseUrl = moduleLicenseUrl,
        )
}
