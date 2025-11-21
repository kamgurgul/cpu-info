/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider {

    actual override fun initLibrary() {
        cpuinfo_initialize()
    }

    actual override fun getCpuName(): String {
        if (!cpuinfo_initialize()) {
            return ""
        }
        return memScoped {
            val cpuInfoGetPackage = cpuinfo_get_package(0.toUInt())
            cpuInfoGetPackage?.pointed?.name?.toKString() ?: ""
        }
    }

    actual override fun hasArmNeon(): Boolean {
        if (!cpuinfo_initialize()) {
            return false
        }
        return cpuinfo_has_arm_neon()
    }

    actual override fun getL1dCaches(): IntArray? {
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

    actual override fun getL1iCaches(): IntArray? {
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

    actual override fun getL2Caches(): IntArray? {
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

    actual override fun getL3Caches(): IntArray? {
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

    actual override fun getL4Caches(): IntArray? {
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

    actual override fun getNumberOfCores(): Int {
        if (!cpuinfo_initialize()) {
            return 1
        }
        return cpuinfo_get_cores_count().toInt()
    }
}
