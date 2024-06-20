package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.StorageDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class StorageDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val storageDataProvider: StorageDataProvider,
) : MutableInteractor<Unit, List<StorageItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        emit(storageDataProvider.getStorageInfo())
    }
}
