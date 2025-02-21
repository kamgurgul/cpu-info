package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.removeNonSpacingMarks
import kotlinx.coroutines.CoroutineDispatcher

class FilterApplicationsInteractor(
    private val dispatchersProvider: IDispatchersProvider
) : ResultInteractor<FilterApplicationsInteractor.Params, List<ExtendedApplicationData>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.default

    override suspend fun doWork(params: Params): List<ExtendedApplicationData> {
        val normalizedQuery = params.searchQuery
            .trim()
            .lowercase()
            .removeNonSpacingMarks()
        return params.applications.filter {
            it.name.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.packageName.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
                || it.versionName.lowercase().removeNonSpacingMarks().contains(normalizedQuery)
        }
    }

    data class Params(
        val applications: List<ExtendedApplicationData>,
        val searchQuery: String,
    )
}
