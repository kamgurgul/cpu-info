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

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.LongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.darwin.sysctlbyname
import platform.posix.size_tVar
import platform.posix.uname
import platform.posix.utsname

actual class CpuDataNativeProvider actual constructor() : ICpuDataNativeProvider {

    actual override fun initLibrary() {
        // No initialization needed for sysctl
    }

    actual override fun getCpuName(): String {
        return sysctlString("machdep.cpu.brand_string").ifEmpty { getMachineIdentifier() }
    }

    actual override fun getL1dCaches(): IntArray? {
        val size = sysctlLong("hw.l1dcachesize")
        return if (size > 0) intArrayOf(size.toInt()) else null
    }

    actual override fun getL1iCaches(): IntArray? {
        val size = sysctlLong("hw.l1icachesize")
        return if (size > 0) intArrayOf(size.toInt()) else null
    }

    actual override fun getL2Caches(): IntArray? {
        val size = sysctlLong("hw.l2cachesize")
        return if (size > 0) intArrayOf(size.toInt()) else null
    }

    actual override fun getL3Caches(): IntArray? {
        val size = sysctlLong("hw.l3cachesize")
        return if (size > 0) intArrayOf(size.toInt()) else null
    }

    actual override fun getL4Caches(): IntArray? {
        // L4 cache is not available via sysctl on iOS
        return null
    }

    actual override fun getNumberOfCores(): Int {
        val cores = sysctlInt("hw.physicalcpu")
        return if (cores > 0) cores else 1
    }

    private fun getMachineIdentifier(): String {
        return memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            systemInfo.machine.toKString()
        }
    }

    private fun sysctlInt(name: String): Int {
        return memScoped {
            val size = alloc<size_tVar>()
            size.value = sizeOf<IntVar>().toULong()
            val value = alloc<IntVar>()
            if (sysctlbyname(name, value.ptr, size.ptr, null, 0u) == 0) {
                value.value
            } else {
                0
            }
        }
    }

    private fun sysctlLong(name: String): Long {
        return memScoped {
            val size = alloc<size_tVar>()
            size.value = sizeOf<LongVar>().toULong()
            val value = alloc<LongVar>()
            if (sysctlbyname(name, value.ptr, size.ptr, null, 0u) == 0) {
                value.value
            } else {
                0L
            }
        }
    }

    private fun sysctlString(name: String): String {
        return memScoped {
            val size = alloc<size_tVar>()
            if (sysctlbyname(name, null, size.ptr, null, 0u) != 0 || size.value == 0uL) {
                return@memScoped ""
            }
            val buffer = allocArray<ByteVar>(size.value.toInt())
            if (sysctlbyname(name, buffer, size.ptr, null, 0u) == 0) {
                buffer.toKString()
            } else {
                ""
            }
        }
    }
}
