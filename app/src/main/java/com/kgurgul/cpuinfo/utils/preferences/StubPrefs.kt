package com.kgurgul.cpuinfo.utils.preferences

class StubPrefs : IPrefs {
    override fun contains(key: String): Boolean {
        return false
    }

    override fun insert(key: String, value: Any) {
    }

    override fun <T> get(key: String, default: T): T {
        return default
    }

    override fun remove(key: String) {
    }
}