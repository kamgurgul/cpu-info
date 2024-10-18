package com.kgurgul.cpuinfo.components

actual class RamCleanupComponent actual constructor() {

    actual fun cleanup() {
    }

    actual fun isCleanupActionAvailable(): Boolean {
        return false
    }
}
