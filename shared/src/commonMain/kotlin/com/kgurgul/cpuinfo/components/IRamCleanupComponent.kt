package com.kgurgul.cpuinfo.components

interface IRamCleanupComponent {

    fun cleanup()

    fun isCleanupActionAvailable(): Boolean
}
