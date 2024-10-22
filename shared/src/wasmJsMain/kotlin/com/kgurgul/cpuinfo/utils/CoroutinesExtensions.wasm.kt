package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.Dispatchers

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    get() = Dispatchers.Default
