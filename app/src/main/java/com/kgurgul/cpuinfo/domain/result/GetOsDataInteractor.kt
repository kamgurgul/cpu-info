package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.OsDataProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetOsDataInteractor @Inject constructor(
    private val dispatchersProvider: IDispatchersProvider,
    private val osDataProvider: OsDataProvider,
) : ResultInteractor<Unit, List<Pair<String, String>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override suspend fun doWork(params: Unit): List<Pair<String, String>> {
        return osDataProvider.getData()
    }
}