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
import android.support.annotation.VisibleForTesting
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for [ProcessesFragment]
 *
 * @author kgurgul
 */
class ProcessesViewModel @Inject constructor(private val prefs: Prefs,
                                             private val psProvider: PsProvider) : ViewModel() {

    companion object {
        private const val SORTING_PROCESSES_KEY = "SORTING_PROCESSES_KEY"
    }

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
            refreshingDisposable = getRefreshingInvoker()
                    .onBackpressureDrop()
                    .flatMapSingle { getSortedProcessListSingle() }
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
     * Return refreshing invoker
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun getRefreshingInvoker(): Flowable<Long> =
            Flowable.interval(0, 5, TimeUnit.SECONDS)

    /**
     * Change process list sorting type from ascending to descending or or vice versa
     */
    fun changeProcessSorting() {
        async(UI) {
            val result = bg { getProcessSortedList(!isSortingAsc) }
            val sortedAppList = result.await()
            ref().processList.replace(sortedAppList)
        }
    }

    /**
     * Save sorting order into [Prefs] and return sorted list of the [ProcessItem]
     *
     * @return sorted list of the processes from [processList]
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    @Synchronized
    internal fun getProcessSortedList(sortingAsc: Boolean): List<ProcessItem> {
        val processListCopy = ArrayList<ProcessItem>(processList)
        isSortingAsc = sortingAsc
        prefs.insert(SORTING_PROCESSES_KEY, sortingAsc)
        if (sortingAsc) {
            processListCopy.sortBy { it.name.toUpperCase() }
        } else {
            processListCopy.sortByDescending { it.name.toUpperCase() }
        }
        return processListCopy
    }

    /**
     * Return [Single] with process list
     */
    private fun getSortedProcessListSingle(): Single<List<ProcessItem>> {
        return psProvider.getPsList()
                .map { processList ->
                    if (processList is ArrayList) {
                        if (isSortingAsc) {
                            processList.sortBy { it.name.toUpperCase() }
                        } else {
                            processList.sortByDescending { it.name.toUpperCase() }
                        }
                    }
                    processList
                }
    }

    override fun onCleared() {
        super.onCleared()
        refreshingDisposable?.dispose()
    }
}