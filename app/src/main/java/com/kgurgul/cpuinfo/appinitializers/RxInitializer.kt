package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject

/**
 * Ignore [UndeliverableException] and [InterruptedException] exceptions. it can be caused by
 * error emitted to the disposed observer or because some blocking code was interrupted by a
 * dispose call.
 */
class RxInitializer @Inject constructor() : AppInitializer {

    override fun init(application: Application) {
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException || e is InterruptedException) {
                Timber.e(e)
                return@setErrorHandler
            }
            throw RuntimeException(e)
        }
    }
}