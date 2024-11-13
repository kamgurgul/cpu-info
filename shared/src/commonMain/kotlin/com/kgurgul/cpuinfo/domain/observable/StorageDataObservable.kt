package com.kgurgul.cpuinfo.domain.observable

import com.kgurgul.cpuinfo.data.provider.IStorageDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow

class StorageDataObservable(
    private val dispatchersProvider: IDispatchersProvider,
    private val storageDataProvider: IStorageDataProvider,
) : MutableInteractor<Unit, List<StorageItem>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    override fun createObservable(params: Unit) = flow {
        emit(storageDataProvider.getStorageInfo())
    }
}
