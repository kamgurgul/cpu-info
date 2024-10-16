package com.kgurgul.cpuinfo.components

expect class RamCleanupComponent() {

    fun cleanup()

    fun isCleanupActionAvailable(): Boolean
}
