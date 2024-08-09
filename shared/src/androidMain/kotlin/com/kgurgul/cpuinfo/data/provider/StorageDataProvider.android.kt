package com.kgurgul.cpuinfo.data.provider

import android.os.Environment
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_24
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.baseline_sd_storage_24
import com.kgurgul.cpuinfo.shared.external
import com.kgurgul.cpuinfo.shared.internal
import com.kgurgul.cpuinfo.utils.CpuLogger
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

@Factory
actual class StorageDataProvider actual constructor() {

    actual fun getStorageInfo(): List<StorageItem> {
        return listOfNotNull(getInternalStorage(), getExternalStorage(), getSdCardStorage())
    }

    private fun getInternalStorage(): StorageItem? {
        val dataDir = Environment.getDataDirectory()
        val storageTotal = dataDir.totalSpace
        val availableSpace = dataDir.usableSpace // Use StorageManager
        val storageUsed = storageTotal - availableSpace
        return if (storageTotal > 0) {
            StorageItem(
                id = "0",
                label = runBlocking { getString(Res.string.internal) + ": " },
                iconDrawable = Res.drawable.baseline_folder_special_24,
                storageTotal = storageTotal,
                storageUsed = storageUsed,
            )
        } else {
            null
        }
    }

    private fun getExternalStorage(): StorageItem? {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val dataDir = Environment.getExternalStorageDirectory()
            val storageTotal = dataDir.totalSpace
            val availableSpace = dataDir.usableSpace // Use StorageManager
            val storageUsed = storageTotal - availableSpace
            if (storageTotal > 0) {
                StorageItem(
                    id = "1",
                    label = runBlocking { getString(Res.string.external) + ": " },
                    iconDrawable = Res.drawable.baseline_folder_24,
                    storageTotal = storageTotal,
                    storageUsed = storageUsed,
                )
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun getSdCardStorage(): StorageItem? {
        getExternalSDMounts().firstOrNull()?.let { sdCardPath ->
            if (sdCardPath.isNotEmpty()) {
                val truncatedPath = if (sdCardPath.contains(":")) {
                    sdCardPath.substring(0, sdCardPath.indexOf(":"))
                } else {
                    sdCardPath
                }
                val externalFilePath = File(truncatedPath)
                if (externalFilePath.exists()) {
                    val storageTotal = externalFilePath.totalSpace
                    val availableSpace = externalFilePath.usableSpace // Use StorageManager
                    val storageUsed = storageTotal - availableSpace
                    return if (storageTotal > 0) {
                        StorageItem(
                            id = "2",
                            label = runBlocking { getString(Res.string.external) + ": " },
                            iconDrawable = Res.drawable.baseline_sd_storage_24,
                            storageTotal = storageTotal,
                            storageUsed = storageUsed,
                        )
                    } else {
                        null
                    }
                }
            }
        }
        return null
    }

    /**
     * And there the magic starts :) TBH I'm not so sure that this is the only good solution but
     * from my testing it is the working one for most of the phones.
     */
    private fun getExternalSDMounts(): List<String> {
        val sdDirList = mutableListOf<String>()
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
                            || strLine.contains("Android/obb"))
                ) {
                    if (strLine.startsWith("/dev/block/vold/")
                        || strLine.startsWith("/dev/block/sd")
                        || strLine.startsWith("/dev/fuse")
                        || strLine.startsWith("/mnt/media_rw")
                    ) {
                        val lineElements = strLine
                            .split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val path = File(lineElements[1])
                        if ((path.exists() || path.isDirectory || path.canWrite())
                            && path.exists()
                            //&& path.canRead()
                            && !path.path.contains("/system")
                            && !sdDirList.contains(lineElements[1])
                            && lineElements[1] != externalDir
                            && lineElements[1] != "/storage/emulated"
                            && !sdDirList.any {
                                it.endsWith(
                                    lineElements[1]
                                        .substring(
                                            lineElements[1].lastIndexOf("/"),
                                            lineElements[1].length
                                        )
                                )
                            }
                        ) {
                            sdDirList.add(lineElements[1])
                        }
                    }
                }
            }
            dis.close()
        } catch (e: Exception) {
            CpuLogger.e(e) { "Cannot read mounts" }
        }
        return sdDirList
    }
}