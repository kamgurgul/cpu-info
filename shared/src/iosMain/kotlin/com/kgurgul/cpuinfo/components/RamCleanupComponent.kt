package com.kgurgul.cpuinfo.components

import org.koin.core.annotation.Factory

@Factory
actual class RamCleanupComponent actual constructor() {

    actual fun cleanup() {

    }

    actual fun isCleanupActionAvailable(): Boolean {
        return false
    }
}