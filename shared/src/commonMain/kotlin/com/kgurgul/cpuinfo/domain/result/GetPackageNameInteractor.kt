package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.PackageNameProvider
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import org.koin.core.annotation.Factory

@Factory
class GetPackageNameInteractor(
    dispatchersProvider: IDispatchersProvider,
    private val packageNameProvider: PackageNameProvider,
) : ResultInteractor<Unit, String>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit): String {
        return packageNameProvider.getPackageName()
    }
}