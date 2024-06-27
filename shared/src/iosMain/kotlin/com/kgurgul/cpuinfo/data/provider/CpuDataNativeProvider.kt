package com.kgurgul.cpuinfo.data.provider

import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libcpuinfo.cpuinfo_get_cores_count
import libcpuinfo.cpuinfo_get_l1d_caches
import libcpuinfo.cpuinfo_get_l1d_caches_count
import libcpuinfo.cpuinfo_get_l1i_caches
import libcpuinfo.cpuinfo_get_l1i_caches_count
import libcpuinfo.cpuinfo_get_l2_caches
import libcpuinfo.cpuinfo_get_l2_caches_count
import libcpuinfo.cpuinfo_get_l3_caches
import libcpuinfo.cpuinfo_get_l3_caches_count
import libcpuinfo.cpuinfo_get_l4_caches
import libcpuinfo.cpuinfo_get_l4_caches_count
import libcpuinfo.cpuinfo_get_package
import libcpuinfo.cpuinfo_has_arm_neon
import libcpuinfo.cpuinfo_initialize
import org.koin.core.annotation.Single

@Single(createdAtStart = true)
actual class CpuDataNativeProvider actual constructor() {

    actual fun initLibrary() {
        cpuinfo_initialize()
    }

    actual fun getCpuName(): String {
        if (!cpuinfo_initialize()) {
            return ""
        }
        return memScoped {
            val cpuInfoGetPackage = cpuinfo_get_package(0.toUInt())
            cpuInfoGetPackage?.pointed?.name?.toKString() ?: ""
        }
    }

    actual fun hasArmNeon(): Boolean {
        if (!cpuinfo_initialize()) {
            return false
        }
        return cpuinfo_has_arm_neon()
    }

    actual fun getL1dCaches(): IntArray? {
        if (!cpuinfo_initialize() || cpuinfo_get_l1d_caches_count() == 0.toUInt()) {
            return null
        }

        return memScoped {
            val cacheCount = cpuinfo_get_l1d_caches_count().toInt()
            val result = IntArray(cacheCount)
            cpuinfo_get_l1d_caches()?.let { l1dCaches ->
                for (i in 0 until cacheCount) {
                    val cache = l1dCaches[i]
                    result[i] = cache.size.toInt()
                }
            }
            result
        }
    }

    actual fun getL1iCaches(): IntArray? {
        if (!cpuinfo_initialize() || cpuinfo_get_l1i_caches_count() == 0.toUInt()) {
            return null
        }

        return memScoped {
            val cacheCount = cpuinfo_get_l1i_caches_count().toInt()
            val result = IntArray(cacheCount)
            cpuinfo_get_l1i_caches()?.let { l1iCaches ->
                for (i in 0 until cacheCount) {
                    val cache = l1iCaches[i]
                    result[i] = cache.size.toInt()
                }
            }
            result
        }
    }

    actual fun getL2Caches(): IntArray? {
        if (!cpuinfo_initialize() || cpuinfo_get_l2_caches_count() == 0.toUInt()) {
            return null
        }

        return memScoped {
            val cacheCount = cpuinfo_get_l2_caches_count().toInt()
            val result = IntArray(cacheCount)
            cpuinfo_get_l2_caches()?.let { l2Caches ->
                for (i in 0 until cacheCount) {
                    val cache = l2Caches[i]
                    result[i] = cache.size.toInt()
                }
            }
            result
        }
    }

    actual fun getL3Caches(): IntArray? {
        if (!cpuinfo_initialize() || cpuinfo_get_l3_caches_count() == 0.toUInt()) {
            return null
        }

        return memScoped {
            val cacheCount = cpuinfo_get_l3_caches_count().toInt()
            val result = IntArray(cacheCount)
            cpuinfo_get_l3_caches()?.let { l3Caches ->
                for (i in 0 until cacheCount) {
                    val cache = l3Caches[i]
                    result[i] = cache.size.toInt()
                }
            }
            result
        }
    }

    actual fun getL4Caches(): IntArray? {
        if (!cpuinfo_initialize() || cpuinfo_get_l4_caches_count() == 0.toUInt()) {
            return null
        }

        return memScoped {
            val cacheCount = cpuinfo_get_l4_caches_count().toInt()
            val result = IntArray(cacheCount)
            cpuinfo_get_l4_caches()?.let { l2Caches ->
                for (i in 0 until cacheCount) {
                    val cache = l2Caches[i]
                    result[i] = cache.size.toInt()
                }
            }
            result
        }
    }

    actual fun getNumberOfCores(): Int {
        if (!cpuinfo_initialize()) {
            return 1
        }
        return cpuinfo_get_cores_count().toInt()
    }
}