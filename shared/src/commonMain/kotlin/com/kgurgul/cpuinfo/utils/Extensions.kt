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

import kotlin.math.round
import kotlin.math.roundToLong

fun Float.round1(): Float = try {
    (this * 10.0).roundToLong() / 10.0f
} catch (e: Exception) {
    0.0f
}

fun Double.round1(): Double = try {
    (this * 10.0).roundToLong() / 10.0
} catch (e: Exception) {
    0.0
}

fun Float.round2(): Float = try {
    (this * 100.0).roundToLong() / 100.0f
} catch (e: Exception) {
    0.0f
}

fun Double.round2(): Double = try {
    (this * 100.0).roundToLong() / 100.0
} catch (e: Exception) {
    0.0
}

fun Float.round4(): Float = try {
    round(this * 10000) / 10000
} catch (e: Exception) {
    0.0f
}

fun Double.round4(): Double = try {
    round(this * 10000) / 10000
} catch (e: Exception) {
    0.0
}

expect fun smartCompare(a: String, b: String): Int

