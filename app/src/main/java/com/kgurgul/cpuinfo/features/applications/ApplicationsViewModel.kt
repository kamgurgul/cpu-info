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

package com.kgurgul.cpuinfo.features.applications

import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.NonNullMutableLiveData
import com.kgurgul.cpuinfo.utils.Prefs
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import com.kgurgul.cpuinfo.utils.wrappers.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for [ApplicationsFragment]
 *
 * @author kgurgul
 */
@HiltViewModel
class ApplicationsViewModel @Inject constructor(
        private val dispatchersProvider: DispatchersProvider,
        private val prefs: Prefs,
        private val packageManager: PackageManager
) : ViewModel() {

    companion object {
        private const val SORTING_APPS_KEY = "SORTING_APPS_KEY"
    }

    val isLoading = NonNullMutableLiveData(false)
    val applicationList = ListLiveData<ExtendedAppInfo>()

    private val _shouldStartStorageServiceEvent = MutableLiveData<Event<Unit>>()
    val shouldStartStorageServiceEvent: LiveData<Event<Unit>>
        get() = _shouldStartStorageServiceEvent

    private var isSortingAsc = prefs.get(SORTING_APPS_KEY, true)
    private var refreshingDisposable: Disposable? = null

    init {
        EventBus.getDefault().register(this)
    }

    @Synchronized
    fun refreshApplicationsList() {
        if (refreshingDisposable == null || !isLoading.value) {
            refreshingDisposable = getApplicationsListSingle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { isLoading.value = true }
                    .doFinally { isLoading.value = false }
                    .subscribe({ appList ->
                        applicationList.replace(appList)
                        runOnApiBelow(Build.VERSION_CODES.O) {
                            _shouldStartStorageServiceEvent.value = Event(Unit)
                        }
                    }, Timber::e)
        }
    }

    /**
     * Get all user applications
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun getApplicationsListSingle(): Single<List<ExtendedAppInfo>> {
        return Single.fromCallable {
            val appsList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                    .map {
                        val hasNativeLibs = if (it.nativeLibraryDir != null) {
                            val fileDir = File(it.nativeLibraryDir)
                            val list = fileDir.listFiles()
                            list != null && list.isNotEmpty()
                        } else false
                        ExtendedAppInfo(it.loadLabel(packageManager).toString(), it.packageName,
                                it.sourceDir, it.nativeLibraryDir, hasNativeLibs,
                                getAppIconUri(it.packageName))
                    }

            return@fromCallable if (isSortingAsc) {
                appsList.sortedBy { it.name.uppercase() }
            } else {
                appsList.sortedByDescending { it.name.uppercase() }
            }
        }
    }

    /**
     * Change apps list sorting type from ascending to descending or or vice versa
     */
    fun changeAppsSorting() {
        viewModelScope.launch {
            val sortedAppList = withContext(dispatchersProvider.io) {
                getAppSortedList(!isSortingAsc)
            }
            applicationList.replace(sortedAppList)
        }
    }

    /**
     * Store passed sorting method into [Prefs] and return sorted list (copy from [applicationList])
     *
     * @return sorted list of the apps from [applicationList]
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun getAppSortedList(sortingAsc: Boolean): List<ExtendedAppInfo> {
        isSortingAsc = sortingAsc
        prefs.insert(SORTING_APPS_KEY, sortingAsc)
        return if (sortingAsc) {
            applicationList.sortedBy { it.name.uppercase() }
        } else {
            applicationList.sortedByDescending { it.name.uppercase() }
        }
    }

    /**
     * Update package size whit specific package name using coroutine
     */
    @Suppress("unused")
    @Subscribe
    fun onUpdatePackageSizeEvent(event: StorageUsageService.UpdatePackageSizeEvent) {
        viewModelScope.launch {
            val newAppPair = withContext(dispatchersProvider.io) {
                getUpdatedApp(event)
            }
            if (newAppPair.first != -1) {
                applicationList[newAppPair.first] = newAppPair.second!!
            }
        }
    }

    /**
     * @return updated [ExtendedAppInfo] instance with corresponding index
     */
    private fun getUpdatedApp(event: StorageUsageService.UpdatePackageSizeEvent)
            : Pair<Int, ExtendedAppInfo?> {
        val app = applicationList.find { it.packageName == event.packageName }
        if (app != null) {
            val index = applicationList.indexOf(app)
            app.appSize = event.size
            return Pair(index, app)
        }
        return Pair(-1, null)
    }

    private fun getAppIconUri(packageName: String): Uri {
        return Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(packageName)
                .path(getResourceId(packageName).toString())
                .build()
    }

    private fun getResourceId(packageName: String): Int {
        val packageInfo: PackageInfo
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return 0
        }
        return packageInfo.applicationInfo.icon
    }

    override fun onCleared() {
        super.onCleared()
        refreshingDisposable?.dispose()
        EventBus.getDefault().unregister(this)
    }
}