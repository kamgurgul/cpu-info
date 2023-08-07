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
