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
        return runCatching {
            licensesProvider.getAll()
                .map { it.toDomain() }
        }
    }

    private fun LicenseData.toDomain() = License(
        moduleName = moduleName,
        moduleVersion = moduleVersion,
        license = moduleLicense,
        licenseUrl = moduleLicenseUrl,
    )
}
