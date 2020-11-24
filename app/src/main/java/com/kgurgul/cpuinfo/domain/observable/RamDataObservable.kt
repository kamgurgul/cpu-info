package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.RamDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RamDataObservable @Inject constructor(
        dispatchersProvider: DispatchersProvider,
        private val ramDataProvider: RamDataProvider
) : ImmutableInteractor<Unit, RamData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val total = ramDataProvider.getTotalBytes()
            val available = ramDataProvider.getAvailableBytes()
            val availablePercentage = ramDataProvider.getAvailablePercentage()
            val threshold = ramDataProvider.getThreshold()
            emit(RamData(total, available, availablePercentage, threshold))
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
    }
}