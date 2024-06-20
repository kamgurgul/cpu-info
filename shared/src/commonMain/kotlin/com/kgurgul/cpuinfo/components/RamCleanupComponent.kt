package com.kgurgul.cpuinfo.components

import org.koin.core.annotation.Factory

@Factory
expect class RamCleanupComponent() {

    fun cleanup()

    fun isCleanupActionAvailable(): Boolean
}