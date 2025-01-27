package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.IRamDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class RamDataObservable(
    dispatchersProvider: IDispatchersProvider,
    private val ramDataProvider: IRamDataProvider,
) : ImmutableInteractor<Unit, RamData>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            val total = ramDataProvider.getTotalBytes()
            val available = ramDataProvider.getAvailableBytes()
            val availablePercentage = if (total != 0L)
                (available.toDouble() / total.toDouble() * 100.0).toInt()
            else 0

            val threshold = ramDataProvider.getThreshold()
            emit(
                RamData(
                    total = total,
                    available = available,
                    availablePercentage = availablePercentage,
                    threshold = threshold,
                    additionalData = ramDataProvider.getAdditionalData(),
                ),
            )
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
    }
}
