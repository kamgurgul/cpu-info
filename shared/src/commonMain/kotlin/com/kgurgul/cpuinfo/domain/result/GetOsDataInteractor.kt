package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.IOsDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class GetOsDataInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val osDataProvider: IOsDataProvider,
) : ImmutableInteractor<Unit, List<Pair<String, String>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            emit(osDataProvider.getData())
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
    }
}
