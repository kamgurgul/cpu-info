package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.HardwareDataProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Factory

@Factory
class GetHardwareDataInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val hardwareDataProvider: HardwareDataProvider,
) : ResultInteractor<Unit, List<Pair<String, String>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override suspend fun doWork(params: Unit): List<Pair<String, String>> {
        return hardwareDataProvider.getData()
    }
}
