package com.kgurgul.cpuinfo.components

expect class RamCleanupComponent() : IRamCleanupComponent {

    override fun cleanup()

    override fun isCleanupActionAvailable(): Boolean
}
