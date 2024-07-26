package com.kgurgul.cpuinfo.utils

import java.text.Collator

actual fun smartCompare(a: String, b: String): Int {
    val collator = Collator.getInstance().apply {
        strength = Collator.SECONDARY
    }
    return collator.compare(a.lowercase(), b.lowercase())
}