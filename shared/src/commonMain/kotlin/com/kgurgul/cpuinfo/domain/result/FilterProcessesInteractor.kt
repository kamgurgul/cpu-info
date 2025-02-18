package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.removeNonSpacingMarks
import kotlinx.coroutines.CoroutineDispatcher

class FilterProcessesInteractor(
    private val dispatchersProvider: IDispatchersProvider
) : ResultInteractor<FilterProcessesInteractor.Params, List<ProcessItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.default

    override suspend fun doWork(params: Params): List<ProcessItem> {
        val normalizedQuery = params.searchQuery
            .trim()
            .lowercase()
            .removeNonSpacingMarks()
        return params.processes.filter {
            it.name.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.pid.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.ppid.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.user.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.niceness.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.rss.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
        }
    }

    data class Params(
        val processes: List<ProcessItem>,
        val searchQuery: String,
    )
}
