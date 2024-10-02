package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.IProcessesProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class ProcessesDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val processesProvider: IProcessesProvider,
) : ImmutableInteractor<ProcessesDataObservable.Params, List<ProcessItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Params) = flow {
        while (true) {
            val processes = processesProvider.getProcessList()
            emit(
                when (params.sortOrder) {
                    SortOrder.ASCENDING -> processes.sorted()
                    SortOrder.DESCENDING -> processes.sortedDescending()
                    else -> processes
                },
            )
            delay(REFRESH_DELAY)
        }
    }

    fun areProcessesSupported() = processesProvider.areProcessesSupported()

    data class Params(
        val sortOrder: SortOrder = SortOrder.NONE,
    )

    companion object {
        private const val REFRESH_DELAY = 3000L
    }
}
