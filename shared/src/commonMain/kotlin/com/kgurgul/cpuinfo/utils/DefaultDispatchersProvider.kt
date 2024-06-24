package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Single

@Single
class DefaultDispatchersProvider : IDispatchersProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}