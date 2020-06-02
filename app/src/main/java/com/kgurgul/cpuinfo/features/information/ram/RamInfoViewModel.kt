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

package com.kgurgul.cpuinfo.features.information.ram

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.Utils
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import com.kgurgul.cpuinfo.utils.runOnApiAbove
import com.opencsv.CSVWriter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.FileWriter
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * ViewModel for RAM info
 *
 * @author kgurgul
 */
class RamInfoViewModel @Inject constructor(
        private val activityManager: ActivityManager,
        private val resources: Resources,
        private val dispatchersProvider: DispatchersProvider,
        private val contentResolver: ContentResolver
) : ViewModel() {

    companion object {
        private const val REFRESHING_INTERVAL = 5L
    }

    private val memoryInfo = ActivityManager.MemoryInfo()
    private var ramRefreshingDisposable: Disposable? = null

    val listLiveData = ListLiveData<Pair<String, String>>()

    @Synchronized
    fun startProvidingData() {
        if (ramRefreshingDisposable == null || ramRefreshingDisposable!!.isDisposed) {
            ramRefreshingDisposable = Flowable.interval(0, REFRESHING_INTERVAL, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ refreshRamData() }, Timber::e)
        }
    }

    fun stopProvidingData() {
        ramRefreshingDisposable?.dispose()
    }

    /**
     * Get RAM info: all, available and threshold
     */
    @SuppressLint("InlinedApi")
    private fun refreshRamData() {
        Timber.i("refreshRamData()")
        val memoryInfoList = mutableListOf<Pair<String, String>>()
        activityManager.getMemoryInfo(memoryInfo)

        runOnApiAbove(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1, {
            val availableMemoryPercent = (memoryInfo.availMem.toDouble()
                    / memoryInfo.totalMem.toDouble() * 100.0).toInt()

            memoryInfoList.add(Pair(resources.getString(R.string.total_memory),
                    Utils.convertBytesToMega(memoryInfo.totalMem)))
            memoryInfoList.add(Pair(resources.getString(R.string.available_memory),
                    "${Utils.convertBytesToMega(memoryInfo.availMem)} ($availableMemoryPercent%)"))
        }, {
            val totalRam = getTotalRamForOldApi()
            val availableMemoryPercent = (memoryInfo.availMem.toDouble()
                    / totalRam.toDouble() * 100.0).toInt()

            memoryInfoList.add(Pair(resources.getString(R.string.total_memory),
                    Utils.convertBytesToMega(totalRam)))
            memoryInfoList.add(Pair(resources.getString(R.string.available_memory),
                    "${Utils.convertBytesToMega(memoryInfo.availMem)} ($availableMemoryPercent%)"))
        })

        memoryInfoList.add(Pair(resources.getString(R.string.threshold),
                Utils.humanReadableByteCount(memoryInfo.threshold)))

        listLiveData.replace(memoryInfoList)
    }

    /**
     * Legacy method for old Android
     */
    private fun getTotalRamForOldApi(): Long {
        var reader: RandomAccessFile? = null
        var totRam: Long = -1
        try {
            reader = RandomAccessFile("/proc/meminfo", "r")
            val load = reader.readLine()

            // Get the Number value from the string
            val p = Pattern.compile("(\\d+)")
            val m = p.matcher(load)
            var value = ""
            while (m.find()) {
                value = m.group(1)
            }
            reader.close()

            totRam = value.toLong()
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            reader?.close()
        }

        return totRam * 1024 // bytes
    }

    /**
     * Run task to clear RAM memory
     */
    fun clearRam() {
        viewModelScope.launch(context = dispatchersProvider.io) {
            System.runFinalization()
            Runtime.getRuntime().gc()
            System.gc()
        }
    }

    /**
     * Invoked when user wants to export whole list to the CSV file
     */
    fun saveListToFile(uri: Uri) {
        viewModelScope.launch(context = dispatchersProvider.io) {
            try {
                contentResolver.openFileDescriptor(uri, "w")?.use {
                    CSVWriter(FileWriter(it.fileDescriptor)).use { csvWriter ->
                        listLiveData.forEach { pair ->
                            csvWriter.writeNext(pair.toList().toTypedArray())
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}