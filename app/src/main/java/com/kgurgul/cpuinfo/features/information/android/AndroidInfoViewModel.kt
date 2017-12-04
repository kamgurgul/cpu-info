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

package com.kgurgul.cpuinfo.features.information.android

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.list.AdapterArrayList
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Android OS info. It is simple container for a lot of static data from Android so
 * it won't required any public methods.
 *
 * @author kgurgul
 */
class AndroidInfoViewModel @Inject constructor(
        private val resources: Resources,
        private val contentResolver: ContentResolver) : ViewModel() {

    val dataObservableList = AdapterArrayList<Pair<String, String>>()

    init {
        getData()
    }

    /**
     * Get all data connected with Android OS
     */
    private fun getData() {
        if (dataObservableList.isNotEmpty()) {
            return
        }
        getBuildData()
        getAndroidIdData()
        getRootData()
        getSecurityData()
    }

    /**
     * Retrieve data from static Build class and system property "java.vm.version"
     */
    private fun getBuildData() {
        dataObservableList.add(Pair(resources.getString(R.string.version), Build.VERSION.RELEASE))
        dataObservableList.add(Pair("SDK", Build.VERSION.SDK_INT.toString()))
        dataObservableList.add(Pair(resources.getString(R.string.codename), Build.VERSION.CODENAME))
        dataObservableList.add(Pair("Bootloader", Build.BOOTLOADER))
        dataObservableList.add(Pair(resources.getString(R.string.brand), Build.BRAND))
        dataObservableList.add(Pair(resources.getString(R.string.model), Build.MODEL))
        dataObservableList.add(Pair(resources.getString(R.string.manufacturer), Build.MANUFACTURER))
        dataObservableList.add(Pair(resources.getString(R.string.board), Build.BOARD))
        dataObservableList.add(Pair("VM", getVmVersion()))
        dataObservableList.add(Pair("Kernel", System.getProperty("os.version") ?: ""))
        @Suppress("DEPRECATION")
        dataObservableList.add(Pair(resources.getString(R.string.serial), Build.SERIAL))
    }

    /**
     * Get AndroidID. Keep in mind that from Android O it is unique per app.
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidIdData() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != null) {
            dataObservableList.add(Pair("Android ID", androidId))
        }
    }

    /**
     * Add information if device is rooted
     */
    private fun getRootData() {
        val isRootedStr = if (isDeviceRooted()) resources.getString(R.string.yes) else
            resources.getString(R.string.no)
        dataObservableList.add(Pair(resources.getString(R.string.rooted), isRootedStr))
    }

    /**
     * Check if device is rooted. Source:
     * https://stackoverflow.com/questions/1101380/determine-if-running-on-a-rooted-device
     */
    private fun isDeviceRooted(): Boolean =
            checkRootMethod1() || checkRootMethod2() || checkRootMethod3()

    private fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootMethod2(): Boolean {
        val paths = arrayOf(
                "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su")
        return paths.any { File(it).exists() }
    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val br = BufferedReader(InputStreamReader(process.inputStream))
            if (br.readLine() != null) return true
            return false
        } catch (t: Throwable) {
            return false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Specify if device is using ART or Dalvik
     */
    private fun getVmVersion(): String {
        var vm = "Dalvik"
        val vmVersion = System.getProperty("java.vm.version")
        if (vmVersion != null && vmVersion.startsWith("2")) {
            vm = "ART"
        }
        return vm
    }

    /**
     * Get information about security providers
     */
    private fun getSecurityData() {
        val securityProviders = getSecurityProviders()
        if (!securityProviders.isEmpty()) {
            dataObservableList.add(Pair(resources.getString(R.string.security_providers), ""))
            dataObservableList.addAll(securityProviders)
        }
    }

    private fun getSecurityProviders(): ArrayList<Pair<String, String>> {
        val functionsList = ArrayList<Pair<String, String>>()

        val providersList = Security.getProviders()
        providersList.forEach {
            functionsList.add(Pair(it.name, it.version.toString()))
        }

        return functionsList
    }
}
