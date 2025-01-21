package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher

class GetLicensesInteractor(
    private val dispatchersProvider: IDispatchersProvider,
) : ResultInteractor<Unit, Result<List<License>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override suspend fun doWork(params: Unit): Result<List<License>> {
        TODO("Not yet implemented")
    }
}
