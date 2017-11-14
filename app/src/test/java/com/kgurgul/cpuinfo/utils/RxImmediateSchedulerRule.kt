package com.kgurgul.cpuinfo.utils

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * Move all rx operations into one thread
 *
 * @author kgurgul
 */
class RxImmediateSchedulerRule : TestRule {

    private val immediate = object : Scheduler() {
        override fun scheduleDirect(run: Runnable, delay: Long, unit: TimeUnit): Disposable =
                super.scheduleDirect(run, 0, unit)

        override fun createWorker(): Scheduler.Worker =
                ExecutorScheduler.ExecutorWorker(Executor { it.run() })
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxJavaPlugins.setInitIoSchedulerHandler { immediate }
                RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
                RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
                RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }

                try {
                    base.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
    }
}
