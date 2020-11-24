package com.kgurgul.cpuinfo.domain.action

import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import javax.inject.Inject

class RamCleanupAction @Inject constructor(
        dispatchersProvider: DispatchersProvider
) : ResultInteractor<Unit, Unit>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit) {
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }
}