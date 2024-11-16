package com.kgurgul.cpuinfo.components

actual class RamCleanupComponent actual constructor() : IRamCleanupComponent {

    actual override fun cleanup() {
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()
    }

    actual override fun isCleanupActionAvailable(): Boolean {
        return false
    }
}
