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
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import com.kgurgul.cpuinfo.CpuInfoApp
import com.kgurgul.cpuinfo.utils.processmanager.AndroidProcesses
import com.kgurgul.cpuinfo.utils.runOnApiBelow
import java.util.*

/**
 * Simple task for all Android which reset most popular apps and runs GC
 *
 * @author kgurgul
 */
class ClearRamAsyncTask : AsyncTask<Void, Void, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        freeRam()
        return null
    }

    private fun freeRam() {
        // GC operations
        System.runFinalization()
        Runtime.getRuntime().gc()
        System.gc()

        // Not my solution - imo bad idea but user wants ram :-)
        // For SDK 24 we can't get running processes list
        runOnApiBelow(24) {
            freeMemoryUsingVoodoo(CpuInfoApp.instance)
        }
    }

    private fun freeMemoryUsingVoodoo(app: Application) {
        val reservedPackages = ArrayList<String>()
        reservedPackages.add("system")
        reservedPackages.add("com.android.launcher2")
        reservedPackages.add("com.android.inputmethod.latin")
        reservedPackages.add("com.android.phone")
        reservedPackages.add("com.android.wallpaper")
        reservedPackages.add("com.google.process.gapps")
        reservedPackages.add("android.process.acore")
        reservedPackages.add("android.process.media")
        reservedPackages.add("com.android.bluetooth")
        reservedPackages.add(app.packageName)

        val packagesToKill = ArrayList<String>()
        val am = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in AndroidProcesses.getRunningAppProcesses()) {
            try {
                val appInfo = process.getPackageInfo(app, 0).applicationInfo
                if (!reservedPackages.contains(process.packageName)
                        && appInfo.flags and ApplicationInfo.FLAG_PERSISTENT == 0) {
                    packagesToKill.add(process.packageName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        for (pck in packagesToKill) {
            am.killBackgroundProcesses(pck)
        }
    }
}