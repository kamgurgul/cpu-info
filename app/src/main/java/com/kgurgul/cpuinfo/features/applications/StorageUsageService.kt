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

@file:Suppress("DEPRECATION")

package com.kgurgul.cpuinfo.features.applications

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.Bundle
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*

/**
 * Get all storage which is used by passed applications. DON'T work on Android O.
 *
 * @author kgurgul
 */
class StorageUsageService : IntentService("StorageUsageService") {

    companion object {
        private const val PACKAGES_LIST_TAG = "packages_list_tag"

        fun startService(context: Context, packagesList: ArrayList<ExtendedAppInfo>) {
            val intent = Intent(context, StorageUsageService::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList(PACKAGES_LIST_TAG, packagesList)
            intent.putExtras(bundle)
            context.startService(intent)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onHandleIntent(intent: Intent?) {
        val packagesList =
            intent?.extras?.getParcelableArrayList<ExtendedAppInfo>(PACKAGES_LIST_TAG)
        packagesList?.forEach {
            getPackageSize(it.packageName, packageManager)
        }
    }

    /**
     * Use reflection to get package size
     */
    private fun getPackageSize(packageName: String, pm: PackageManager) {
        try {
            val getPackageSizeInfo = pm.javaClass.getMethod(
                "getPackageSizeInfo",
                String::class.java, IPackageStatsObserver::class.java
            )
            getPackageSizeInfo.invoke(pm, packageName, object : IPackageStatsObserver.Stub() {
                override fun onGetStatsCompleted(pStats: PackageStats, succeeded: Boolean) {
                    sendSizeEvent(pStats)
                }
            })
        } catch (e: Exception) {
            Timber.d("Cannot get package size for: $packageName")
        }
    }

    @Synchronized
    fun sendSizeEvent(pStats: PackageStats) {
        val size = pStats.codeSize + pStats.dataSize + pStats.cacheSize
        Timber.d("Size for: $packageName - $size")
        EventBus.getDefault().post(UpdatePackageSizeEvent(pStats.packageName, size))
    }

    data class UpdatePackageSizeEvent(val packageName: String, val size: Long)
}