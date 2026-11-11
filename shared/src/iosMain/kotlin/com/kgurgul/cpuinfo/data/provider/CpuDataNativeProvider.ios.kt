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

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.cpu_cache_line_size
import com.kgurgul.cpuinfo.shared.cpu_efficiency_cores
import com.kgurgul.cpuinfo.shared.cpu_logical_cores
import com.kgurgul.cpuinfo.shared.cpu_page_size
import com.kgurgul.cpuinfo.shared.cpu_performance_cores
import com.kgurgul.cpuinfo.utils.Utils
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
        val brandString = sysctlString("machdep.cpu.brand_string")
        if (brandString.isNotEmpty()) {
            return brandString
        }
        val machine = getMachineIdentifier()
        return resolveAppleSocName(machine) ?: machine
    }

    actual override fun getL1dCaches(): IntArray? {
        return getPerfLevelCaches("l1dcachesize")
            ?: sysctlLong("hw.l1dcachesize").takeIf { it > 0 }?.let { intArrayOf(it.toInt()) }
    }

    actual override fun getL1iCaches(): IntArray? {
        return getPerfLevelCaches("l1icachesize")
            ?: sysctlLong("hw.l1icachesize").takeIf { it > 0 }?.let { intArrayOf(it.toInt()) }
    }

    actual override fun getL2Caches(): IntArray? {
        return getPerfLevelCaches("l2cachesize")
            ?: sysctlLong("hw.l2cachesize").takeIf { it > 0 }?.let { intArrayOf(it.toInt()) }
    }

    actual override fun getL3Caches(): IntArray? {
        return getPerfLevelCaches("l3cachesize")
            ?: sysctlLong("hw.l3cachesize").takeIf { it > 0 }?.let { intArrayOf(it.toInt()) }
    }

    actual override fun getL4Caches(): IntArray? {
        // L4 cache is not available via sysctl on iOS
        return null
    }

    actual override fun getNumberOfCores(): Int {
        val cores = sysctlInt("hw.physicalcpu")
        return if (cores > 0) cores else 1
    }

    fun getNumberOfLogicalCores(): Int {
        val cores = sysctlInt("hw.logicalcpu")
        return if (cores > 0) cores else getNumberOfCores()
    }

    override fun getExtraItems(): List<ItemValue> {
        return buildList {
            val logicalCores = getNumberOfLogicalCores()
            if (logicalCores > 0 && logicalCores != getNumberOfCores()) {
                add(ItemValue.NameResource(Res.string.cpu_logical_cores, logicalCores.toString()))
            }
            val nPerfLevels = sysctlInt("hw.nperflevels")
            if (nPerfLevels >= 1) {
                val perfCores = sysctlInt("hw.perflevel0.physicalcpu")
                if (perfCores > 0) {
                    add(
                        ItemValue.NameResource(
                            Res.string.cpu_performance_cores,
                            perfCores.toString(),
                        )
                    )
                }
            }
            if (nPerfLevels >= 2) {
                val effCores = sysctlInt("hw.perflevel1.physicalcpu")
                if (effCores > 0) {
                    add(
                        ItemValue.NameResource(
                            Res.string.cpu_efficiency_cores,
                            effCores.toString(),
                        )
                    )
                }
            }
            val cacheLineSize = sysctlLong("hw.cachelinesize")
            if (cacheLineSize > 0) {
                add(
                    ItemValue.NameResource(
                        Res.string.cpu_cache_line_size,
                        Utils.humanReadableByteCount(cacheLineSize),
                    )
                )
            }
            val pageSize = sysctlLong("hw.pagesize")
            if (pageSize > 0) {
                add(
                    ItemValue.NameResource(
                        Res.string.cpu_page_size,
                        Utils.humanReadableByteCount(pageSize),
                    )
                )
            }
        }
    }

    private fun getPerfLevelCaches(cacheKey: String): IntArray? {
        val nPerfLevels = sysctlInt("hw.nperflevels")
        if (nPerfLevels <= 0) return null
        val caches = buildList {
            for (level in 0 until nPerfLevels) {
                val size = sysctlLong("hw.perflevel$level.$cacheKey")
                if (size > 0) {
                    add(size.toInt())
                }
            }
        }
        return caches.takeIf { it.isNotEmpty() }?.toIntArray()
    }

    internal fun getMachineIdentifier(): String {
        val hwMachine = sysctlString("hw.machine")
        if (hwMachine.isNotEmpty()) {
            return hwMachine
        }
        return memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            systemInfo.machine.toKString()
        }
    }

    private fun resolveAppleSocName(machine: String): String? {
        return when {
            machine.startsWith("iPhone17,1") || machine.startsWith("iPhone17,2") -> "Apple A18 Pro"
            machine.startsWith("iPhone17,") -> "Apple A18"
            machine.startsWith("iPhone16,") -> "Apple A17 Pro"
            machine.startsWith("iPhone15,") || machine == "iPhone14,7" || machine == "iPhone14,8" ->
                if (
                    machine == "iPhone15,2" ||
                    machine == "iPhone15,3" ||
                    machine == "iPhone15,4" ||
                    machine == "iPhone15,5"
                ) {
                    "Apple A16 Bionic"
                } else {
                    "Apple A15 Bionic"
                }

            machine.startsWith("iPhone14,") -> "Apple A15 Bionic"
            machine.startsWith("iPhone13,") -> "Apple A14 Bionic"
            machine.startsWith("iPhone12,") -> "Apple A13 Bionic"
            machine.startsWith("iPhone11,") -> "Apple A12 Bionic"
            machine.startsWith("iPhone10,") -> "Apple A11 Bionic"
            machine.startsWith("iPad16,3") ||
                machine.startsWith("iPad16,4") ||
                machine.startsWith("iPad16,5") ||
                machine.startsWith("iPad16,6") -> "Apple M4"

            machine.startsWith("iPad16,1") || machine.startsWith("iPad16,2") -> "Apple A17 Pro"
            machine.startsWith("iPad15,") -> "Apple M3"
            machine.startsWith("iPad14,1") || machine.startsWith("iPad14,2") -> "Apple A15 Bionic"
            machine.startsWith("iPad14,") -> "Apple M2"
            machine == "iPad13,1" ||
                machine == "iPad13,2" ||
                machine == "iPad13,18" ||
                machine == "iPad13,19" -> "Apple A14 Bionic"

            machine.startsWith("iPad13,") -> "Apple M1"
            else -> null
        }
    }

    internal fun sysctlInt(name: String): Int {
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

    internal fun sysctlLong(name: String): Long {
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

    internal fun sysctlString(name: String): String {
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
