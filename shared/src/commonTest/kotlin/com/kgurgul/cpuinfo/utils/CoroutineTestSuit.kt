/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

class CoroutineTestSuit(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
) {

    val testDispatcherProvider =
        object : IDispatchersProvider {
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
