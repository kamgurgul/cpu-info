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
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.utils.lifecycleawarelist.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security
import javax.inject.Inject

/**
 * ViewModel for Android OS info. It is simple container for a lot of static data from Android so
 * it won't required any public methods.
 *
 * @author kgurgul
 */
@HiltViewModel
class AndroidInfoViewModel @Inject constructor(
    application: Application,
    private val resources: Resources,
    private val contentResolver: ContentResolver,
    private val devicePolicyManager: DevicePolicyManager
) : AndroidViewModel(application) {

    val listLiveData = ListLiveData<Pair<String, String>>()

    init {
        getData()
    }

    /**
     * Get all data connected with Android OS
     */
    private fun getData() {
        if (listLiveData.isNotEmpty()) {
            return
        }
        getBuildData()
        getAndroidIdData()
        getGsfAndroidId()
        getRootData()
        getDeviceEncryptionStatus()
        getStrongBoxData()
        getSecurityData()
    }

    /**
     * Retrieve data from static Build class and system property "java.vm.version"
     */
    @SuppressLint("HardwareIds")
    private fun getBuildData() {
        listLiveData.add(Pair(resources.getString(R.string.version), Build.VERSION.RELEASE))
        listLiveData.add(Pair("SDK", Build.VERSION.SDK_INT.toString()))
        listLiveData.add(Pair(resources.getString(R.string.codename), Build.VERSION.CODENAME))
        listLiveData.add(Pair("Bootloader", Build.BOOTLOADER))
        listLiveData.add(Pair(resources.getString(R.string.brand), Build.BRAND))
        listLiveData.add(Pair(resources.getString(R.string.model), Build.MODEL))
        listLiveData.add(Pair(resources.getString(R.string.manufacturer), Build.MANUFACTURER))
        listLiveData.add(Pair(resources.getString(R.string.board), Build.BOARD))
        listLiveData.add(Pair("VM", getVmVersion()))
        listLiveData.add(Pair("Kernel", System.getProperty("os.version") ?: ""))
        @Suppress("DEPRECATION")
        listLiveData.add(Pair(resources.getString(R.string.serial), Build.SERIAL))
    }

    /**
     * Get AndroidID. Keep in mind that from Android O it is unique per app.
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidIdData() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != null) {
            listLiveData.add(Pair("Android ID", androidId))
        }
    }

    /**
     * Add information if device is rooted
     */
    private fun getRootData() {
        val isRootedStr = if (isDeviceRooted()) resources.getString(R.string.yes) else
            resources.getString(R.string.no)
        listLiveData.add(Pair(resources.getString(R.string.rooted), isRootedStr))
    }

    /**
     * Add information about device encrypted storage status
     */
    private fun getDeviceEncryptionStatus() {
        try {
            val status = devicePolicyManager.storageEncryptionStatus
            val statusText = when (status) {
                DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED -> ENCRYPTION_STATUS_UNSUPPORTED
                DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> ENCRYPTION_STATUS_INACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING -> ENCRYPTION_STATUS_ACTIVATING
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE -> ENCRYPTION_STATUS_ACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ->
                    ENCRYPTION_STATUS_ACTIVE_PER_USER
                else -> resources.getString(R.string.unknown)
            }
            listLiveData.add(Pair(resources.getString(R.string.encrypted_storage), statusText))
        } catch (ignored: Exception) {
        }
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
            "/system/bin/failsafe/su", "/data/local/su"
        )
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
        val securityProviders = Security.getProviders().map { Pair(it.name, it.version.toString()) }
        if (securityProviders.isNotEmpty()) {
            listLiveData.add(Pair(resources.getString(R.string.security_providers), ""))
            listLiveData.addAll(securityProviders)
        }
    }

    private fun getGsfAndroidId() {
        val uri = Uri.parse("content://com.google.android.gsf.gservices")
        val idKey = "android_id"
        val params = arrayOf(idKey)
        try {
            getApplication<Application>().contentResolver.query(
                uri, null, null, params, null
            )?.use {
                it.moveToFirst()
                val hexId = java.lang.Long.toHexString(it.getString(1).toLong())
                listLiveData.add(Pair("Google Services Framework ID", hexId))
            }
        } catch (e: Exception) {
            // Do nothing
        }
    }

    private fun getStrongBoxData() {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= 28) {
            getApplication<Application>().packageManager
                .hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }
        listLiveData.add(Pair("StrongBox", getYesNoString(hasStrongBox)))
    }

    private fun getYesNoString(value: Boolean) = if (value) {
        resources.getString(R.string.yes)
    } else {
        resources.getString(R.string.no)
    }

    companion object {
        private const val ENCRYPTION_STATUS_UNSUPPORTED = "UNSUPPORTED"
        private const val ENCRYPTION_STATUS_INACTIVE = "INACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVATING = "ACTIVATING"
        private const val ENCRYPTION_STATUS_ACTIVE = "ACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVE_PER_USER = "ACTIVE_PER_USER"
    }
}
