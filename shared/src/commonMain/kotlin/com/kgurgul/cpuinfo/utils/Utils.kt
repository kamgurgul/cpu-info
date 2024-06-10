/*
 * Copyright 2017 KG Soft
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

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToLong

object Utils {

    fun humanReadableByteCount(
        byteCount: Long,
        decimalPlaces: Int = 2,
        zeroPadFraction: Boolean = false
    ): String {
        require(byteCount > Long.MIN_VALUE) { "Out of range" }
        require(decimalPlaces >= 0) { "Negative decimal places unsupported" }
        val isNegative = byteCount < 0
        val absByteCount = abs(byteCount)
        return if (absByteCount < 1024) {
            "${byteCount}B"
        } else {
            val zeroBitCount: Int = (63 - absByteCount.countLeadingZeroBits()) / 10
            val absNumber: Double = absByteCount.toDouble() / (1L shl zeroBitCount * 10)
            val roundingFactor: Int = 10.0.pow(decimalPlaces).toInt()
            val absRoundedNumberString =
                with((absNumber * roundingFactor).roundToLong().toString()) {
                    val splitIndex = length - decimalPlaces - 1
                    val wholeString = substring(0..splitIndex)
                    val fractionString = with(substring(splitIndex + 1)) {
                        if (zeroPadFraction) this else dropLastWhile { digit -> digit == '0' }
                    }
                    if (fractionString.isEmpty()) wholeString else "$wholeString.$fractionString"
                }
            val roundedNumberString =
                if (isNegative) "-$absRoundedNumberString" else absRoundedNumberString
            "$roundedNumberString${"KMGTPE"[zeroBitCount - 1]}B"
        }
    }

    fun convertBytesToMega(bytes: Long): String {
        val megaBytes = bytes.toDouble() / (1024.0 * 1024.0)
        val roundedMegaBytes = round(megaBytes * 100) / 100
        val formatted = roundedMegaBytes.toString()
            .dropLastWhile { it == '0' }
            .dropLastWhile { it == '.' }
        return "${formatted}MB"
    }
}
