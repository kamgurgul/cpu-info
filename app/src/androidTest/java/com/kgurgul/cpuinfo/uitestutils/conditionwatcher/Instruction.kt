package com.kgurgul.cpuinfo.uitestutils.conditionwatcher

import android.os.Bundle

/**
 * Base for all instructions. [description] is used for logging purpose and you can pass data via
 * [dataContainer]. [checkCondition] method controls the instruction state.
 */
abstract class Instruction {

    var dataContainer = Bundle()

    abstract val description: String

    abstract fun checkCondition(): Boolean
}