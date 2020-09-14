package com.kgurgul.cpuinfo.data.provider

import android.os.Build
import timber.log.Timber
import java.io.File
import java.io.FileFilter
import java.io.RandomAccessFile
import java.util.regex.Pattern
import javax.inject.Inject

class CpuDataProvider @Inject constructor() {

    fun getAbi(): String {
        return if (Build.VERSION.SDK_INT >= 21) {
            Build.SUPPORTED_ABIS[0]
        } else {
            @Suppress("DEPRECATION")
            Build.CPU_ABI
        }
    }

    fun getNumberOfCores(): Int {
        return if (Build.VERSION.SDK_INT >= 17) {
            Runtime.getRuntime().availableProcessors()
        } else {
            getNumCoresLegacy()
        }
    }

    /**
     * Checking frequencies directories and return current value if exists (otherwise we can
     * assume that core is stopped - value -1)
     */
    fun getCurrentFreq(coreNumber: Int): Long {
        val currentFreqPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/scaling_cur_freq"
        return try {
            RandomAccessFile(currentFreqPath, "r").use { it.readLine().toLong() / 1000 }
        } catch (e: Exception) {
            Timber.e("getCurrentFreq() - cannot read file")
            -1
        }
    }

    /**
     * Read max/min frequencies for specific [coreNumber]. Return [Pair] with min and max frequency
     * or [Pair] with -1.
     */
    fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        val minPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_min_freq"
        val maxPath = "${CPU_INFO_DIR}cpu$coreNumber/cpufreq/cpuinfo_max_freq"
        return try {
            val minMhz = RandomAccessFile(minPath, "r").use { it.readLine().toLong() / 1000 }
            val maxMhz = RandomAccessFile(maxPath, "r").use { it.readLine().toLong() / 1000 }
            Pair(minMhz, maxMhz)
        } catch (e: Exception) {
            Timber.e("getMinMaxFreq() - cannot read file")
            Pair(-1, -1)
        }
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if check fails
     */
    private fun getNumCoresLegacy(): Int {
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.name)) {
                    return true
                }
                return false
            }
        }
        return try {
            val dir = File(CPU_INFO_DIR)
            val files = dir.listFiles(CpuFilter())
            files.size
        } catch (e: Exception) {
            1
        }
    }

    companion object {
        private const val CPU_INFO_DIR = "/sys/devices/system/cpu/"
    }
}