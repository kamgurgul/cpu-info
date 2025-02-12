package com.kgurgul.cpuinfo.utils

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping
import platform.Foundation.localizedCompare

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun smartCompare(a: String, b: String): Int {
    return (a as NSString).localizedCompare(b).toInt()
}

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun String.normalize(): String {
    return (this as NSString).decomposedStringWithCanonicalMapping
}
