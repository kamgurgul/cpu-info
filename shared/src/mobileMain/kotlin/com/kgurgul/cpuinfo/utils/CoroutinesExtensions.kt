package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

actual val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
    get() = Dispatchers.IO
