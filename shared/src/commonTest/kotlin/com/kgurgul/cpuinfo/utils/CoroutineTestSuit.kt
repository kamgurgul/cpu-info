package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class CoroutineTestSuit(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler()),
) {

    val testDispatcherProvider = object : IDispatchersProvider {
        override val main: CoroutineDispatcher
            get() = testDispatcher
        override val io: CoroutineDispatcher
            get() = testDispatcher
        override val default: CoroutineDispatcher
            get() = testDispatcher
        override val unconfined: CoroutineDispatcher
            get() = testDispatcher
    }

    fun onStart() {
        Dispatchers.setMain(testDispatcher)
    }

    fun onFinished() {
        Dispatchers.resetMain()
    }
}
