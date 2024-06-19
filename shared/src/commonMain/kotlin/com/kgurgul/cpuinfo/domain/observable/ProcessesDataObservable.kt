package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.ProcessesProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class ProcessesDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val processesProvider: ProcessesProvider,
) : ImmutableInteractor<Unit, List<ProcessItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            emit(processesProvider.getProcessList())
            delay(REFRESH_DELAY)
        }
    }

    fun areProcessesSupported() = processesProvider.areProcessesSupported()

    companion object {
        private const val REFRESH_DELAY = 3000L
    }
}