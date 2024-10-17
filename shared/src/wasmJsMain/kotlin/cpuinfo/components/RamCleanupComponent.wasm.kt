package com.kgurgul.cpuinfo.components

actual class RamCleanupComponent actual constructor() {

    actual fun cleanup() {
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }

    actual fun isCleanupActionAvailable(): Boolean {
        return false
    }
}
