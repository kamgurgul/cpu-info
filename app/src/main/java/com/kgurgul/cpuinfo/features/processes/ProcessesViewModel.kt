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

package com.kgurgul.cpuinfo.features.processes

import android.arch.lifecycle.ViewModel
import com.kgurgul.cpuinfo.common.Prefs
import com.kgurgul.cpuinfo.common.list.AdapterArrayList
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for [ProcessesFragment]
 *
 * @author kgurgul
 */
class ProcessesViewModel @Inject constructor(private val prefs: Prefs) : ViewModel() {

    private val SORTING_PROCESSES_KEY = "SORTING_PROCESSES_KEY"

    val processList = AdapterArrayList<ProcessItem>()

    private val ref = asReference()
    private var isSortingAsc = prefs.get(SORTING_PROCESSES_KEY, true)
    private var refreshingDisposable: Disposable? = null

    /**
     * Start refreshing processes (every 5s)
     */
    @Synchronized
    fun startProcessRefreshing() {
        if (refreshingDisposable == null || refreshingDisposable?.isDisposed == true) {
            refreshingDisposable = Flowable.interval(0, 5, TimeUnit.SECONDS)
                    .onBackpressureDrop()
                    .flatMapSingle { getProcessListSingle() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ newProcessList ->
                        Timber.i("Processes refreshed")
                        processList.replace(newProcessList)
                    }, { throwable ->
                        Timber.e(throwable)
                    })
        }
    }

    /**
     * Stop processes refreshing.
     */
    fun stopProcessRefreshing() {
        refreshingDisposable?.dispose()
    }

    /**
     * Change process list sorting type from ascending to descending or or vice versa
     */
    fun changeProcessSorting() {
        async(UI) {
            val result = bg { getAppSortedList() }
            val sortedAppList = result.await()
            ref().processList.replace(sortedAppList)
        }
    }

    /**
     * @return sorted list of the apps from [processList]
     */
    private fun getAppSortedList(): List<ProcessItem> {
        val processListCopy = ArrayList<ProcessItem>(processList)
        isSortingAsc = !isSortingAsc
        prefs.insert(SORTING_PROCESSES_KEY, isSortingAsc)
        if (isSortingAsc) {
            processListCopy.sortBy { it.name.toUpperCase() }
        } else {
            processListCopy.sortByDescending { it.name.toUpperCase() }
        }
        return processListCopy
    }

    /**
     * Return [Single] with process list
     */
    private fun getProcessListSingle(): Single<List<ProcessItem>> {
        return Single.fromCallable({
            val processesList = ArrayList<ProcessItem>()
            processesList.addAll(readPs())
            if (isSortingAsc) {
                processesList.sortBy { it.name.toUpperCase() }
            } else {
                processesList.sortByDescending { it.name.toUpperCase() }
            }
            processesList
        })
    }

    /**
     * Old method for reading ps output. Should be rewritten.
     */
    private fun readPs(): List<ProcessItem> {
        val processesList = ArrayList<ProcessItem>()

        try {
            val args = arrayListOf("/system/bin/ps", "-p")
            val cmd = ProcessBuilder(args)

            val process = cmd.start()
            val bis = process.inputStream.bufferedReader()

            val lines = bis.readLines()
            lines.forEachIndexed { i, line ->
                if (i > 0) {
                    val st = StringTokenizer(line)

                    // It is bad. Refactor this!
                    var iterator = 0
                    var user = ""
                    var name = ""
                    var pid = ""
                    var ppid = ""
                    var niceness = ""
                    var rss = ""
                    var vsize = ""

                    while (st.hasMoreTokens()) {
                        when (iterator) {
                            0 -> user = st.nextToken()
                            1 -> pid = st.nextToken()
                            2 -> ppid = st.nextToken()
                            3 -> vsize = st.nextToken()
                            4 -> rss = st.nextToken()
                            6 -> niceness = st.nextToken()
                            12 -> name = st.nextToken()
                            else -> st.nextToken()
                        }
                        iterator++
                    }

                    processesList.add(ProcessItem(name, pid, ppid, niceness, user, rss, vsize))
                }
            }

            bis.close()
        } catch (e: IOException) {
            Timber.e(e)
        }

        return processesList
    }

    override fun onCleared() {
        super.onCleared()
        refreshingDisposable?.dispose()
    }
}