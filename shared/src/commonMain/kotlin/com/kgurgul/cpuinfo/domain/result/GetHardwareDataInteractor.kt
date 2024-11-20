package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.HardwareDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class GetHardwareDataInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val hardwareDataProvider: HardwareDataProvider,
) : MutableInteractor<Unit, List<ItemValue>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        while (true) {
            emit(hardwareDataProvider.getData())
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 5000L
    }
}
