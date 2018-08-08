/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.features.information.cpu

import android.arch.lifecycle.ViewModel
import android.os.Build
import android.support.annotation.UiThread
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * ViewModel for CPU information
 *
 * @author kgurgul
 */
class CpuInfoViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val REFRESHING_INTERVAL = 1L
        private const val FREQUENCY_OFFSET = 1
    }

    private val excludedTags = listOf("processor", "BogoMIPS")
    private val minMaxFreqMap = mutableMapOf<Int, Pair<Long, Long>>()
    private var refreshingDisposable: Disposable? = null

    val listLiveData = ListLiveData<Pair<String, String>>()

    @Synchronized
    fun startProvidingData() {
        if (listLiveData.isEmpty()) {
            val cpuInfoList = ArrayList<Pair<String, String>>()
            cpuInfoList.add(getAbi())
            cpuInfoList.addAll(getCoresInfo())
            cpuInfoList.addAll(getCpuInfoFromFile())
            listLiveData.addAll(cpuInfoList)
        }

        if (refreshingDisposable == null || refreshingDisposable?.isDisposed != false) {
            refreshingDisposable = Flowable.interval(REFRESHING_INTERVAL, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ refreshFrequencies() }, Timber::e)
        }
    }

    fun stopProvidingData() {
        refreshingDisposable?.dispose()
    }

    /**
     * Get current CPU frequency and refresh [listLiveData]
     */
    @UiThread
    private fun refreshFrequencies() {
        Timber.i("refreshFrequencies()")
        val freqList = getCoresInfo()
        freqList.forEachIndexed { i, pair -> listLiveData[i + FREQUENCY_OFFSET] = pair }
    }

    /**
     * Get current ABI
     */
    private fun getAbi(): Pair<String, String> {
        val abi: String
        if (Build.VERSION.SDK_INT >= 21) {
            abi = Build.SUPPORTED_ABIS[0]
        } else {
            @Suppress("DEPRECATION")
            abi = Build.CPU_ABI
        }
        return Pair("ABI", abi)
    }

    /**
     * Get all information about CPU cores amount and theirs frequencies
     */
    private fun getCoresInfo(): ArrayList<Pair<String, String>> {
        val coresList = ArrayList<Pair<String, String>>()
        val numbersOfCores = getNumberOfCores()
        coresList.add(Pair("Cores", numbersOfCores.toString()))
        for (i in 0 until numbersOfCores) {
            coresList.add(Pair("     Core $i", tryToGetCurrentFreq(i)))
        }
        return coresList
    }

    /**
     * Get global info for cpu from /proc/cpuinfo. Ignore cores because of buggy value on some
     * devices.
     */
    private fun getCpuInfoFromFile(): ArrayList<Pair<String, String>> {
        val featuresList = ArrayList<Pair<String, String>>()

        val cpuInfoFile = File("/proc/cpuinfo")
        if (cpuInfoFile.exists()) {
            try {
                val br = BufferedReader(FileReader(cpuInfoFile))
                var aLine: String?

                aLine = br.readLine()
                while (aLine != null) {
                    var stringParts = aLine.split(":")
                    if (stringParts.size == 2) {
                        stringParts = stringParts.map { it.trim() }

                        if (!excludedTags.contains(stringParts[0])) {
                            featuresList.add(Pair(stringParts[0].capitalize(), stringParts[1]))
                        }
                    }
                    aLine = br.readLine()
                }

                br.close()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
        return featuresList
    }

    /**
     * Checking frequencies directories and return current value if exists (otherwise we can
     * assume that core is stopped)
     */
    private fun tryToGetCurrentFreq(coreNumber: Int): String {
        var frequency = "Stopped"
        val filePath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/scaling_cur_freq"

        try {
            val reader = RandomAccessFile(filePath, "r")
            val value = reader.readLine().toLong() / 1000
            reader.close()
            frequency = "${value}MHz"

            if (minMaxFreqMap.containsKey(coreNumber)) {
                frequency += "\n(${minMaxFreqMap[coreNumber]?.first}MHz - ${minMaxFreqMap[coreNumber]?.second}MHz)"
            } else {
                val minMaxPair = tryToGetMinMaxFreq(coreNumber)
                if (minMaxPair != null) {
                    frequency += "\n(${minMaxPair.first}MHz - ${minMaxPair.second}MHz)"
                }
            }
        } catch (ignored: Exception) {
        }

        return frequency
    }

    /**
     * Read max/min frequencies for all cores
     */
    private fun tryToGetMinMaxFreq(coreNumber: Int): Pair<Long, Long>? {
        val minPath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/cpuinfo_min_freq"
        val maxPath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/cpuinfo_max_freq"
        try {
            var reader = RandomAccessFile(minPath, "r")
            val minMhz = reader.readLine().toLong() / 1000
            reader.close()

            reader = RandomAccessFile(maxPath, "r")
            val maxMhz = reader.readLine().toLong() / 1000
            reader.close()
            val values = Pair(minMhz, maxMhz)
            minMaxFreqMap[coreNumber] = values
            return values
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    /**
     * Get number of CPU cores
     */
    private fun getNumberOfCores(): Int {
        return if (Build.VERSION.SDK_INT >= 17) {
            Runtime.getRuntime().availableProcessors()
        } else {
            getNumCoresOldPhones()
        }
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    private fun getNumCoresOldPhones(): Int {
        // Private Class to display only CPU devices in the directory listing
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
            // Get directory containing CPU info
            val dir = File("/sys/devices/system/cpu/")
            // Filter to only list the devices we care about
            val files = dir.listFiles(CpuFilter())
            // Return the number of cores (virtual CPU devices)
            files.size
        } catch (e: Exception) {
            // Default is 1 core
            1
        }
    }
}