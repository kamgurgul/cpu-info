package com.kgurgul.cpuinfo.data.provider

import android.os.Environment
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.StorageItem
import timber.log.Timber
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject

class StorageDataProvider @Inject constructor() {

    fun getStorageInfo(): List<StorageItem> {
        return listOfNotNull(getInternalStorage(), getExternalStorage(), getSdCardStorage())
    }

    private fun getInternalStorage(): StorageItem? {
        val dataDir = Environment.getDataDirectory()
        val storageTotal = dataDir.totalSpace
        val availableSpace = dataDir.usableSpace
        val storageUsed = storageTotal - availableSpace
        return if (storageTotal > 0) {
            StorageItem(
                labelRes = R.string.internal,
                iconRes = R.drawable.root,
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
            val availableSpace = dataDir.usableSpace
            val storageUsed = storageTotal - availableSpace
            if (storageTotal > 0) {
                StorageItem(
                    labelRes = R.string.external,
                    iconRes = R.drawable.folder,
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
                    val availableSpace = externalFilePath.usableSpace
                    val storageUsed = storageTotal - availableSpace
                    return if (storageTotal > 0) {
                        StorageItem(
                            labelRes = R.string.external,
                            iconRes = R.drawable.sdcard,
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
            Timber.i(e)
        }
        return sdDirList
    }
}