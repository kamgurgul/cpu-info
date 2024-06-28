package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.ApplicationsDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.SortOrder
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.wrapToResultFlow
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ApplicationsDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val applicationsDataProvider: ApplicationsDataProvider,
) : MutableInteractor<ApplicationsDataObservable.Params, Result<List<ExtendedApplicationData>>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Params): Flow<Result<List<ExtendedApplicationData>>> {
        return wrapToResultFlow {
            val apps = applicationsDataProvider.getInstalledApplications(params.withSystemApps)
            when (params.sortOrder) {
                SortOrder.ASCENDING -> apps.sorted()
                SortOrder.DESCENDING -> apps.sortedDescending()
                else -> apps
            }
        }
    }

    fun areApplicationsSupported() = applicationsDataProvider.areApplicationsSupported()

    data class Params(
        val withSystemApps: Boolean,
        val sortOrder: SortOrder = SortOrder.NONE
    )
}