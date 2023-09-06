package com.kgurgul.cpuinfo.utils.preferences

interface IPrefs {

    fun contains(key: String): Boolean
    fun insert(key: String, value: Any)
    fun <T> get(key: String, default: T): T
    fun remove(key: String)
}