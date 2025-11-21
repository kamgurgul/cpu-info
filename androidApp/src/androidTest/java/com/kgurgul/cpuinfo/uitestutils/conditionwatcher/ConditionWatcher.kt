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
package com.kgurgul.cpuinfo.uitestutils.conditionwatcher

import java.util.concurrent.TimeoutException

@Synchronized
fun waitForCondition(timeoutMillis: Long = 5000L, condition: () -> Boolean) {
    val step = 250L
    var totalTime = 0L

    do {
        if (condition()) {
            break
        }
        totalTime += step
        Thread.sleep(step)
        if (totalTime > timeoutMillis) {
            throw TimeoutException("Condition timed out after: ${totalTime}ms")
        }
    } while (true)
}
