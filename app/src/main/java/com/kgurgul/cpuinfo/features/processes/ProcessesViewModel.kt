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

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.Prefs
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for [ProcessesFragment]
 *
 * @author kgurgul
 */
@HiltViewModel
class ProcessesViewModel @Inject constructor(
        private val dispatchersProvider: DispatchersProvider,
        private val prefs: Prefs,
        private val psProvider: PsProvider
) : ViewModel() {

    companion object {
        private const val SORTING_PROCESSES_KEY = "SORTING_PROCESSES_KEY"
    }

    val processList = ListLiveData<ProcessItem>()

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
                    }, Timber::e)
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
        viewModelScope.launch {
            val sortedAppList = withContext(dispatchersProvider.io) {
                getProcessSortedList(!isSortingAsc)
            }
            processList.replace(sortedAppList)
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
            processListCopy.sortBy { it.name.uppercase() }
        } else {
            processListCopy.sortByDescending { it.name.uppercase() }
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
                            processList.sortBy { it.name.uppercase() }
                        } else {
                            processList.sortByDescending { it.name.uppercase() }
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