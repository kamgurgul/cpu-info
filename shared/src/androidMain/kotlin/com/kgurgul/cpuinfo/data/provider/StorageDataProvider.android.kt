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

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.baseline_sd_storage_24
import com.kgurgul.cpuinfo.shared.internal
import com.kgurgul.cpuinfo.utils.CpuLogger
import java.io.File
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class StorageDataProvider actual constructor() : IStorageDataProvider, KoinComponent {

    private val context: Context by inject()
    private val storageManager: StorageManager by inject()

    actual override suspend fun getStorageInfo(): List<StorageItem> = buildList {
        val internal = getInternalStorage()
        internal?.let(::add)
        for ((volume, directory) in getMountedVolumes()) {
            // The emulated primary volume lives on /data, so it would duplicate internal storage
            if (internal != null && volume.isPrimary && volume.isEmulated && volume.uuid == null) {
                continue
            }
            getVolumeStorage(directory, TextResource.Text(volume.getDescription(context)))
                ?.let(::add)
        }
    }

    private fun getInternalStorage(): StorageItem? {
        val dataDirectory = Environment.getDataDirectory()
        val (total, available) =
            getInternalStorageStats() ?: (dataDirectory.totalSpace to dataDirectory.usableSpace)
        if (total <= 0) return null
        return StorageItem(
            id = dataDirectory.absolutePath,
            label = TextResource.Resource(Res.string.internal),
            iconDrawable = Res.drawable.baseline_folder_special_24,
            storageTotal = total,
            storageUsed = total - available,
        )
    }

    /**
     * Returns total and free bytes of the whole internal storage, matching the values presented in
     * the system settings. Unlike statfs on /data, the total includes the system partitions.
     */
    private fun getInternalStorageStats(): Pair<Long, Long>? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return null
        return try {
            val statsManager =
                context.getSystemService(StorageStatsManager::class.java) ?: return null
            val total = statsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            val free = statsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            if (total > 0 && free in 0..total) total to free else null
        } catch (e: Exception) {
            CpuLogger.w(e) { "Cannot read internal storage stats" }
            null
        }
    }

    /**
     * Returns mounted shared storage volumes with a directory that can be used to query their size.
     * App-specific directories cover the primary storage and SD cards on every API level. Since API
     * 30 volume roots are also available, which adds volumes without app-specific directories, e.g.
     * USB drives.
     */
    private fun getMountedVolumes(): List<Pair<StorageVolume, File>> {
        val appDirectories =
            context.getExternalFilesDirs(null).filterNotNull().mapNotNull { directory ->
                storageManager.getStorageVolume(directory)?.let { volume -> volume to directory }
            }
        val volumes =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val knownRoots = appDirectories.mapNotNull { (volume, _) -> volume.directory }
                appDirectories +
                    storageManager.storageVolumes.mapNotNull { volume ->
                        volume.directory?.takeIf { it !in knownRoots }?.let { volume to it }
                    }
            } else {
                appDirectories
            }
        return volumes.filter { (volume, _) ->
            volume.state == Environment.MEDIA_MOUNTED ||
                volume.state == Environment.MEDIA_MOUNTED_READ_ONLY
        }
    }

    private fun getVolumeStorage(directory: File, label: TextResource): StorageItem? {
        val total = directory.totalSpace
        if (total <= 0) return null
        return StorageItem(
            id = directory.absolutePath,
            label = label,
            iconDrawable = Res.drawable.baseline_sd_storage_24,
            storageTotal = total,
            storageUsed = total - directory.usableSpace,
        )
    }
}
