package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dispatchers used by coroutines
 *
 * @author kgurgul
 */
@Singleton
class DispatchersProvider @Inject constructor() {
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
}