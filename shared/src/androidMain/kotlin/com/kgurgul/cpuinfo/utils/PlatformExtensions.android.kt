package com.kgurgul.cpuinfo.utils

import java.text.Collator
import java.text.Normalizer

actual fun smartCompare(a: String, b: String): Int {
    val collator = Collator.getInstance().apply {
        strength = Collator.SECONDARY
    }
    return collator.compare(a.lowercase(), b.lowercase())
}

actual fun String.normalize(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
}
