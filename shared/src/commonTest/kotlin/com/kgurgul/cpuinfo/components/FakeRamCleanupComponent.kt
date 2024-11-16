package com.kgurgul.cpuinfo.components

class FakeRamCleanupComponent(
    var cleanupActionAvailable: Boolean = false,
) : IRamCleanupComponent {

    var cleanupInvoked = false
        private set

    override fun cleanup() {
        cleanupInvoked = true
    }

    override fun isCleanupActionAvailable(): Boolean {
        return cleanupActionAvailable
    }

    fun reset() {
        cleanupInvoked = false
    }
}
