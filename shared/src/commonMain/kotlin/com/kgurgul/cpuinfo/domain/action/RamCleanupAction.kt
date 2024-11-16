package com.kgurgul.cpuinfo.domain.action

import com.kgurgul.cpuinfo.components.IRamCleanupComponent
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider

class RamCleanupAction(
    dispatchersProvider: IDispatchersProvider,
    private val ramCleanupComponent: IRamCleanupComponent,
) : ResultInteractor<Unit, Unit>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit) {
        ramCleanupComponent.cleanup()
    }

    fun isCleanupActionAvailable(): Boolean {
        return ramCleanupComponent.isCleanupActionAvailable()
    }
}
