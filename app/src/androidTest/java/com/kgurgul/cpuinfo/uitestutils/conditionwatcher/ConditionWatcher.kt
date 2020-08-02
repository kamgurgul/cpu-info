package com.kgurgul.cpuinfo.uitestutils.conditionwatcher

import timber.log.Timber

/**
 * Global singleton which can block test execution until [Instruction] condition is met or timeout
 * occurs
 */
object ConditionWatcher {

    private const val DEFAULT_TIMEOUT_LIMIT = 1000 * 60
    private const val DEFAULT_INTERVAL = 250
    private const val CONDITION_NOT_MET = 0
    private const val CONDITION_MET = 1
    private const val TIMEOUT = 2

    private var timeoutLimit = DEFAULT_TIMEOUT_LIMIT
    private var watchInterval = DEFAULT_INTERVAL

    @Synchronized
    fun waitForCondition(instruction: Instruction) {
        var status = CONDITION_NOT_MET
        var elapsedTime = 0

        do {
            val condition = instruction.checkCondition()
            Timber.d("Desc: %s, Condition status: %s", instruction.description, condition)
            if (condition) {
                status = CONDITION_MET
            } else {
                elapsedTime += watchInterval
                Thread.sleep(watchInterval.toLong())
            }

            if (elapsedTime >= timeoutLimit) {
                status = TIMEOUT
                break
            }
        } while (status != CONDITION_MET)

        if (status == TIMEOUT) {
            throw Exception(instruction.description + " - took more than " + timeoutLimit / 1000 + " seconds. Test stopped.")
        }
    }

    fun setWatchInterval(watchInterval: Int) {
        ConditionWatcher.watchInterval = watchInterval
    }

    fun setTimeoutLimit(ms: Int) {
        timeoutLimit = ms
    }
}
