package com.kgurgul.cpuinfo.utils

import kotlin.js.Promise

actual fun smartCompare(a: String, b: String): Int {
    val collator = Intl.Collator()
    return collator.compare(a.lowercase(), b.lowercase())
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
