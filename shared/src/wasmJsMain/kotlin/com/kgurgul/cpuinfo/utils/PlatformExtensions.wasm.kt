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

import kotlin.js.Promise

actual fun smartCompare(a: String, b: String): Int {
    val collator = Intl.Collator()
    return collator.compare(a.lowercase(), b.lowercase())
}

actual fun String.normalize(): String {
    return jsNormalize(this.toJsString()).toString()
}

fun JsArray<out JsString>.toList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.length) {
        this[i]?.let { list.add(it.toString()) }
    }
    return list
}

external class Intl {
    class Collator {
        fun compare(a: String, b: String): Int
    }
}

external fun getUsedHeapSize(): Int

external fun getTotalHeapSize(): Int

external fun getTotalStorage(): Promise<JsBigInt>

external fun getUsedStorage(): Promise<JsBigInt>

external fun isPdfViewerEnabled(): Boolean

external fun jsNormalize(value: JsString): JsString
