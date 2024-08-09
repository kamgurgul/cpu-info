package com.kgurgul.cpuinfo.data.provider

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.android_id
import com.kgurgul.cpuinfo.shared.board
import com.kgurgul.cpuinfo.shared.bootloader
import com.kgurgul.cpuinfo.shared.brand
import com.kgurgul.cpuinfo.shared.codename
import com.kgurgul.cpuinfo.shared.encrypted_storage
import com.kgurgul.cpuinfo.shared.google_services_framework_id
import com.kgurgul.cpuinfo.shared.kernel
import com.kgurgul.cpuinfo.shared.manufacturer
import com.kgurgul.cpuinfo.shared.model
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.rooted
import com.kgurgul.cpuinfo.shared.sdk
import com.kgurgul.cpuinfo.shared.security_providers
import com.kgurgul.cpuinfo.shared.serial
import com.kgurgul.cpuinfo.shared.strongbox
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.unknown
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.shared.vm
import com.kgurgul.cpuinfo.shared.yes
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security

@Factory
actual class OsDataProvider actual constructor() : KoinComponent {

    private val contentResolver: ContentResolver by inject()
    private val packageManager: PackageManager by inject()
    private val devicePolicyManager: DevicePolicyManager by inject()

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.tab_os) to "Android")
            addAll(getBuildData())
            getAndroidIdData()?.let { add(it) }
            getGsfAndroidId()?.let { add(it) }
            add(getString(Res.string.rooted) to getYesNoString(isDeviceRooted()))
            getDeviceEncryptionStatus()?.let { add(it) }
            add(getStrongBoxData())
            addAll(getSecurityData())
        }
    }

    /**
     * Retrieve data from static Build class and system property "java.vm.version"
     */
    @SuppressLint("HardwareIds")
    private suspend fun getBuildData(): List<Pair<String, String>> {
        return buildList {
            add(getString(Res.string.version) to Build.VERSION.RELEASE)
            add(getString(Res.string.sdk) to Build.VERSION.SDK_INT.toString())
            add(getString(Res.string.codename) to Build.VERSION.CODENAME)
            add(getString(Res.string.bootloader) to Build.BOOTLOADER)
            add(getString(Res.string.brand) to Build.BRAND)
            add(getString(Res.string.model) to Build.MODEL)
            add(getString(Res.string.manufacturer) to Build.MANUFACTURER)
            add(getString(Res.string.board) to Build.BOARD)
            add(getString(Res.string.vm) to getVmVersion())
            add(getString(Res.string.kernel) to (System.getProperty("os.version") ?: ""))
            @Suppress("DEPRECATION")
            add(getString(Res.string.serial) to Build.SERIAL)
        }
    }

    /**
     * Get AndroidID. Keep in mind that from Android O it is unique per app.
     */
    @SuppressLint("HardwareIds")
    private suspend fun getAndroidIdData(): Pair<String, String>? {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return if (androidId != null) {
            getString(Res.string.android_id) to androidId
        } else {
            null
        }
    }

    /**
     * Add information about device encrypted storage status
     */
    @Suppress("DEPRECATION")
    private suspend fun getDeviceEncryptionStatus(): Pair<String, String>? {
        return try {
            val statusText = when (devicePolicyManager.storageEncryptionStatus) {
                DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED -> ENCRYPTION_STATUS_UNSUPPORTED
                DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> ENCRYPTION_STATUS_INACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING -> ENCRYPTION_STATUS_ACTIVATING
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE -> ENCRYPTION_STATUS_ACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ->
                    ENCRYPTION_STATUS_ACTIVE_PER_USER

                else -> getString(Res.string.unknown)
            }
            getString(Res.string.encrypted_storage) to statusText
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
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val br = BufferedReader(InputStreamReader(process.inputStream))
            br.readLine() != null
        } catch (t: Throwable) {
            false
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
    private suspend fun getSecurityData(): List<Pair<String, String>> {
        val securityProviders = Security.getProviders().map { Pair(it.name, it.version.toString()) }
        return buildList {
            if (securityProviders.isNotEmpty()) {
                add(getString(Res.string.security_providers) to "")
                addAll(securityProviders)
            }
        }
    }

    private suspend fun getGsfAndroidId(): Pair<String, String>? {
        val uri = Uri.parse("content://com.google.android.gsf.gservices")
        val idKey = "android_id"
        val params = arrayOf(idKey)
        return try {
            contentResolver.query(uri, null, null, params, null)?.use {
                it.moveToFirst()
                val hexId = java.lang.Long.toHexString(it.getString(1).toLong())
                getString(Res.string.google_services_framework_id) to hexId
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getStrongBoxData(): Pair<String, String> {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= 28) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }
        return getString(Res.string.strongbox) to getYesNoString(hasStrongBox)
    }

    private suspend fun getYesNoString(value: Boolean) = if (value) {
        getString(Res.string.yes)
    } else {
        getString(Res.string.no)
    }

    companion object {
        private const val ENCRYPTION_STATUS_UNSUPPORTED = "UNSUPPORTED"
        private const val ENCRYPTION_STATUS_INACTIVE = "INACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVATING = "ACTIVATING"
        private const val ENCRYPTION_STATUS_ACTIVE = "ACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVE_PER_USER = "ACTIVE_PER_USER"
    }
}