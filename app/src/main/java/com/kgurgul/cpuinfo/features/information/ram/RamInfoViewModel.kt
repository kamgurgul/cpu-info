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

import android.app.ActivityManager
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Build
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.AdapterArrayList
import com.kgurgul.cpuinfo.utils.Utils
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.io.RandomAccessFile
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * ViewModel for RAM info
 *
 * @author kgurgul
 */
class RamInfoViewModel @Inject constructor(private val activityManager: ActivityManager,
                                           private val resources: Resources) : ViewModel() {

    companion object {
        private const val REFRESHING_INTERVAL = 5L
    }

    private val memoryInfo = ActivityManager.MemoryInfo()
    private var ramRefreshingDisposable: Disposable? = null
    private var clearRamAsyncTask: ClearRamAsyncTask? = null

    val dataObservableList = AdapterArrayList<Pair<String, String>>()

    @Synchronized
    fun startProvidingData() {
        if (ramRefreshingDisposable == null || ramRefreshingDisposable!!.isDisposed) {
            ramRefreshingDisposable = Flowable.interval(0, REFRESHING_INTERVAL, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ -> refreshRamData() },
                            { e -> Timber.e(e) })
        }
    }

    fun stopProvidingData() {
        ramRefreshingDisposable?.dispose()
    }

    /**
     * Get RAM info: all, available and threshold
     */
    private fun refreshRamData() {
        Timber.i("refreshRamData()")
        val memoryInfoList = ArrayList<Pair<String, String>>()
        activityManager.getMemoryInfo(memoryInfo)

        if (Build.VERSION.SDK_INT >= 16) {
            val availableMemoryPercent = (memoryInfo.availMem.toDouble()
                    / memoryInfo.totalMem.toDouble() * 100.0).toInt()

            memoryInfoList.add(Pair(resources.getString(R.string.total_memory),
                    Utils.convertBytesToMega(memoryInfo.totalMem)))
            memoryInfoList.add(Pair(resources.getString(R.string.available_memory),
                    "${Utils.convertBytesToMega(memoryInfo.availMem)} ($availableMemoryPercent%)"))
        } else {
            val totalRam = getTotalRamForOldApi()
            val availableMemoryPercent = (memoryInfo.availMem.toDouble()
                    / totalRam.toDouble() * 100.0).toInt()

            memoryInfoList.add(Pair(resources.getString(R.string.total_memory),
                    Utils.convertBytesToMega(totalRam)))
            memoryInfoList.add(Pair(resources.getString(R.string.available_memory),
                    "${Utils.convertBytesToMega(memoryInfo.availMem)} ($availableMemoryPercent%)"))
        }

        memoryInfoList.add(Pair(resources.getString(R.string.threshold),
                Utils.humanReadableByteCount(memoryInfo.threshold)))

        dataObservableList.replace(memoryInfoList)
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
        if (clearRamAsyncTask == null || clearRamAsyncTask?.status != AsyncTask.Status.RUNNING) {
            clearRamAsyncTask?.cancel(true)
            clearRamAsyncTask = ClearRamAsyncTask()
            clearRamAsyncTask?.execute()
        }
    }
}