package com.kgurgul.cpuinfo.components

actual class RamCleanupComponent actual constructor() : IRamCleanupComponent {

    actual override fun cleanup() {
    }

    actual override fun isCleanupActionAvailable(): Boolean {
        return false
    }
}
