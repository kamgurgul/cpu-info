package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchersProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}