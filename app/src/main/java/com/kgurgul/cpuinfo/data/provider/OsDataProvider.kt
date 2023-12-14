package com.kgurgul.cpuinfo.data.provider

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.kgurgul.cpuinfo.R
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security
import javax.inject.Inject

class OsDataProvider @Inject constructor(
    private val resources: Resources,
    private val contentResolver: ContentResolver,
    private val packageManager: PackageManager,
    private val devicePolicyManager: DevicePolicyManager,
) {

    fun getData(): List<Pair<String, String>> {
        return buildList {
            addAll(getBuildData())
            getAndroidIdData()?.let { add(it) }
            getGsfAndroidId()?.let { add(it) }
            add(resources.getString(R.string.rooted) to getYesNoString(isDeviceRooted()))
            getDeviceEncryptionStatus()?.let { add(it) }
            add(getStrongBoxData())
            addAll(getSecurityData())
        }
    }

    /**
     * Retrieve data from static Build class and system property "java.vm.version"
     */
    @SuppressLint("HardwareIds")
    private fun getBuildData(): List<Pair<String, String>> {
        return buildList {
            add(resources.getString(R.string.version) to Build.VERSION.RELEASE)
            add(resources.getString(R.string.sdk) to Build.VERSION.SDK_INT.toString())
            add(resources.getString(R.string.codename) to Build.VERSION.CODENAME)
            add(resources.getString(R.string.bootloader) to Build.BOOTLOADER)
            add(resources.getString(R.string.brand) to Build.BRAND)
            add(resources.getString(R.string.model) to Build.MODEL)
            add(resources.getString(R.string.manufacturer) to Build.MANUFACTURER)
            add(resources.getString(R.string.board) to Build.BOARD)
            add(resources.getString(R.string.vm) to getVmVersion())
            add(resources.getString(R.string.kernel) to (System.getProperty("os.version") ?: ""))
            @Suppress("DEPRECATION")
            add(resources.getString(R.string.serial) to Build.SERIAL)
        }
    }

    /**
     * Get AndroidID. Keep in mind that from Android O it is unique per app.
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidIdData(): Pair<String, String>? {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return if (androidId != null) {
            resources.getString(R.string.android_id) to androidId
        } else {
            null
        }
    }

    /**
     * Add information about device encrypted storage status
     */
    @Suppress("DEPRECATION")
    private fun getDeviceEncryptionStatus(): Pair<String, String>? {
        return try {
            val statusText = when (devicePolicyManager.storageEncryptionStatus) {
                DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED -> ENCRYPTION_STATUS_UNSUPPORTED
                DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> ENCRYPTION_STATUS_INACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING -> ENCRYPTION_STATUS_ACTIVATING
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE -> ENCRYPTION_STATUS_ACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ->
                    ENCRYPTION_STATUS_ACTIVE_PER_USER

                else -> resources.getString(R.string.unknown)
            }
            resources.getString(R.string.encrypted_storage) to statusText
        } catch (ignored: Exception) {
            null
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
            return br.readLine() != null
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
    private fun getSecurityData(): List<Pair<String, String>> {
        val securityProviders = Security.getProviders().map { Pair(it.name, it.version.toString()) }
        return buildList {
            if (securityProviders.isNotEmpty()) {
                add(resources.getString(R.string.security_providers) to "")
                addAll(securityProviders)
            }
        }
    }

    private fun getGsfAndroidId(): Pair<String, String>? {
        val uri = Uri.parse("content://com.google.android.gsf.gservices")
        val idKey = "android_id"
        val params = arrayOf(idKey)
        return try {
            contentResolver.query(uri, null, null, params, null)?.use {
                it.moveToFirst()
                val hexId = java.lang.Long.toHexString(it.getString(1).toLong())
                resources.getString(R.string.google_services_framework_id) to hexId
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getStrongBoxData(): Pair<String, String> {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= 28) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }
        return resources.getString(R.string.strongbox) to getYesNoString(hasStrongBox)
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