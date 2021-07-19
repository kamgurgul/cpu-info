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

package com.kgurgul.cpuinfo.features.information.storage

import android.content.res.Resources
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for [StorageInfoFragment]
 *
 * @author kgurgul
 */
@HiltViewModel
class StorageInfoViewModel @Inject constructor(
        private val dispatchersProvider: DispatchersProvider,
        private val resources: Resources
) : ViewModel() {

    enum class MemoryType { INTERNAL, EXTERNAL }

    val listLiveData = ListLiveData<StorageItem>()

    private var sdCardFinderDisposable: Disposable? = null

    init {
        getStorageInfo()
    }

    /**
     * Get all available details about internal, external and secondary (SD card) storage
     */
    private fun getStorageInfo() {
        viewModelScope.launch {
            val memoryPair = withContext(dispatchersProvider.io) {
                getExternalAndInternalMemoryPair()
            }
            listLiveData.add(memoryPair.first)
            if (memoryPair.second != null) {
                listLiveData.add(memoryPair.second as StorageItem)
            }
            refreshSdCard()
        }
    }

    /**
     * @return pair of [StorageItem] where on the first place is internal storage and on the second
     * external
     */
    private fun getExternalAndInternalMemoryPair(): Pair<StorageItem, StorageItem?> {
        val internalTotal = getTotalMemorySize(MemoryType.INTERNAL)
        val internalUsed = internalTotal - getAvailableMemorySize(MemoryType.INTERNAL)
        val internalMemory = StorageItem(resources.getString(R.string.internal), R.drawable.root,
                internalTotal, internalUsed)

        var externalMemory: StorageItem? = null
        if (isExternalMemoryAvailable()) {
            val externalTotal = getTotalMemorySize(MemoryType.EXTERNAL)
            val externalUsed = externalTotal - getAvailableMemorySize(MemoryType.EXTERNAL)
            externalMemory = StorageItem(resources.getString(R.string.external),
                    R.drawable.folder, externalTotal, externalUsed)
        }
        return Pair(internalMemory, externalMemory)
    }

    /**
     * Try to get new data about SD card or remove it from the list
     */
    @Synchronized
    fun refreshSdCard() {
        if (sdCardFinderDisposable == null || sdCardFinderDisposable!!.isDisposed) {
            sdCardFinderDisposable = getSDCardFinder()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ sdCardFile ->
                        if (sdCardFile.totalSpace > 0) {
                            val sdTotal = sdCardFile.totalSpace
                            val sdUsed = sdTotal - sdCardFile.usableSpace
                            val sdMemory = StorageItem(resources.getString(R.string.external),
                                    R.drawable.sdcard, sdTotal, sdUsed)
                            listLiveData.add(sdMemory)
                        }
                    }, {
                        Timber.i("Cannot find SD card file")
                        val storageItem = listLiveData.find { storageItem ->
                            storageItem.iconRes == R.drawable.sdcard
                        }
                        if (storageItem != null) {
                            listLiveData.remove(storageItem)
                        }
                    })
        }
    }

    /**
     * @return true if device support external storage, otherwise false
     */
    private fun isExternalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Get total bytes from internal or external storage.
     *
     * @param memoryType type of memory
     * @return full size of the storage
     */
    @Suppress("DEPRECATION")
    private fun getTotalMemorySize(memoryType: MemoryType): Long {
        val path: File = when (memoryType) {
            MemoryType.INTERNAL -> Environment.getDataDirectory()
            MemoryType.EXTERNAL -> Environment.getExternalStorageDirectory()
        }

        return path.totalSpace
    }

    /**
     * Get available bytes from internal or external storage.
     *
     * @param memoryType type of memory
     * @return available size of the storage
     */
    @Suppress("DEPRECATION")
    private fun getAvailableMemorySize(memoryType: MemoryType): Long {
        val path: File = when (memoryType) {
            MemoryType.INTERNAL -> Environment.getDataDirectory()
            MemoryType.EXTERNAL -> Environment.getExternalStorageDirectory()
        }

        return path.usableSpace
    }

    /**
     * @return [Single] with the SD card file or onError in case of missing that one.
     */
    private fun getSDCardFinder(): Single<File> {
        return Single.create { emitter: SingleEmitter<File> ->
            val mountedList = getExternalSDMounts()

            var strSDCardPath: String? = null
            if (mountedList.size > 0) {
                strSDCardPath = mountedList[0]
            }

            if (strSDCardPath != null && strSDCardPath.isNotEmpty()) {
                if (strSDCardPath.contains(":")) {
                    strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"))
                }

                val externalFilePath = File(strSDCardPath)

                if (externalFilePath.exists() && !emitter.isDisposed) {
                    emitter.onSuccess(externalFilePath)
                } else if (!emitter.isDisposed) {
                    emitter.onError(FileNotFoundException("Cannot find SD card file"))
                }
            } else if (!emitter.isDisposed) {
                emitter.onError(FileNotFoundException("Cannot find SD card file"))
            }
        }
    }

    /**
     * And there the magic starts :) TBH I'm not so sure that this is the only good solution but
     * from my testing it is the working one for most of the phones.
     */
    @Suppress("DEPRECATION")
    private fun getExternalSDMounts(): ArrayList<String> {
        val sdDirList = ArrayList<String>()
        try {
            val dis = DataInputStream(FileInputStream("/proc/mounts"))
            val br = BufferedReader(InputStreamReader(dis))
            val externalDir = Environment.getExternalStorageDirectory().path
            while (true) {
                val strLine = br.readLine()
                if (strLine == null) {
                    break
                } else if (!(strLine.contains("asec")
                        || strLine.contains("legacy")
                        || strLine.contains("Android/obb"))) {
                    if (strLine.startsWith("/dev/block/vold/")
                            || strLine.startsWith("/dev/block/sd")
                            || strLine.startsWith("/dev/fuse")
                            || strLine.startsWith("/mnt/media_rw")) {
                        val lineElements =
                                strLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                        val path = File(lineElements[1])
                        if ((path.exists()
                                || path.isDirectory
                                || path.canWrite())
                                && path.exists()
                                //&& path.canRead()
                                && !path.path.contains("/system")
                                && !sdDirList.contains(lineElements[1])
                                && lineElements[1] != externalDir
                                && lineElements[1] != "/storage/emulated"
                                && !sdDirList.any {
                            it.endsWith(lineElements[1]
                                    .substring(lineElements[1].lastIndexOf("/"),
                                            lineElements[1].length))
                        }) {
                            sdDirList.add(lineElements[1])
                        }
                    }
                }
            }
            dis.close()
        } catch (e: Exception) {
            Timber.i(e)
        }

        return sdDirList
    }

    override fun onCleared() {
        super.onCleared()
        sdCardFinderDisposable?.dispose()
    }
}