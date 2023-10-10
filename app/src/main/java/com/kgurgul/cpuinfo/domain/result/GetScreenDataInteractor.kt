package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.ScreenDataProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetScreenDataInteractor @Inject constructor(
    private val dispatchersProvider: IDispatchersProvider,
    private val screenDataProvider: ScreenDataProvider,
) : ResultInteractor<Unit, List<Pair<String, String>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override suspend fun doWork(params: Unit): List<Pair<String, String>> {
        return screenDataProvider.getData()
    }
}