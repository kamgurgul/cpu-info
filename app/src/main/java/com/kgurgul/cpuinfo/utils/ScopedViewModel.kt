package com.kgurgul.cpuinfo.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel with [CoroutineScope] which is responsible for job lifecycle
 *
 * @author kgurgul
 */
open class ScopedViewModel(
        private val dispatchersProvider: DispatchersProvider
) : ViewModel(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + dispatchersProvider.mainDispatcher

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}