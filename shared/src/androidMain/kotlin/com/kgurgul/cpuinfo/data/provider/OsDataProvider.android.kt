package com.kgurgul.cpuinfo.data.provider

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.kgurgul.cpuinfo.domain.model.ItemValue
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
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.rooted
import com.kgurgul.cpuinfo.shared.sdk
import com.kgurgul.cpuinfo.shared.security_providers
import com.kgurgul.cpuinfo.shared.serial
import com.kgurgul.cpuinfo.shared.strongbox
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.shared.vm
import com.kgurgul.cpuinfo.utils.ResourceUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security
import java.util.Locale
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class OsDataProvider actual constructor() :
    IOsDataProvider,
    KoinComponent {

    private val contentResolver: ContentResolver by inject()
    private val packageManager: PackageManager by inject()
    private val devicePolicyManager: DevicePolicyManager by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.tab_os, "Android"))
            addAll(getBuildData())
            add(ItemValue.NameResource(Res.string.os_language, Locale.getDefault().language))
            getAndroidIdData()?.let { add(it) }
            getGsfAndroidId()?.let { add(it) }
            add(
                ItemValue.NameValueResource(
                    Res.string.rooted,
                    ResourceUtils.getYesNoStringResource(isDeviceRooted())
                )
            )
            getDeviceEncryptionStatus()?.let { add(it) }
            add(getStrongBoxData())
            addAll(getSecurityData())
        }
    }

    /**
     * Retrieve data from static Build class and system property "java.vm.version"
     */
    @SuppressLint("HardwareIds")
    private fun getBuildData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.version, Build.VERSION.RELEASE))
            add(ItemValue.NameResource(Res.string.sdk, Build.VERSION.SDK_INT.toString()))
            add(ItemValue.NameResource(Res.string.codename, Build.VERSION.CODENAME))
            add(ItemValue.NameResource(Res.string.bootloader, Build.BOOTLOADER))
            add(ItemValue.NameResource(Res.string.brand, Build.BRAND))
            add(ItemValue.NameResource(Res.string.model, Build.MODEL))
            add(ItemValue.NameResource(Res.string.manufacturer, Build.MANUFACTURER))
            add(ItemValue.NameResource(Res.string.board, Build.BOARD))
            add(ItemValue.NameResource(Res.string.vm, getVmVersion()))
            add(ItemValue.NameResource(Res.string.kernel, (System.getProperty("os.version") ?: "")))
            @Suppress("DEPRECATION")
            add(ItemValue.NameResource(Res.string.serial, Build.SERIAL))
        }
    }

    /**
     * Get AndroidID. Keep in mind that from Android O it is unique per app.
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidIdData(): ItemValue? {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return if (androidId != null) {
            ItemValue.NameResource(Res.string.android_id, androidId)
        } else {
            null
        }
    }

    /**
     * Add information about device encrypted storage status
     */
    @Suppress("DEPRECATION")
    private fun getDeviceEncryptionStatus(): ItemValue? {
        return try {
            val statusText = when (devicePolicyManager.storageEncryptionStatus) {
                DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED -> ENCRYPTION_STATUS_UNSUPPORTED
                DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> ENCRYPTION_STATUS_INACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING -> ENCRYPTION_STATUS_ACTIVATING
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE -> ENCRYPTION_STATUS_ACTIVE
                DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER ->
                    ENCRYPTION_STATUS_ACTIVE_PER_USER

                else -> ENCRYPTION_STATUS_UNKNOWN
            }
            ItemValue.NameResource(Res.string.encrypted_storage, statusText)
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
            "/system/bin/failsafe/su", "/data/local/su",
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
    private fun getSecurityData(): List<ItemValue> {
        val securityProviders = Security.getProviders()
            .map { ItemValue.Text(it.name, it.version.toString()) }
        return buildList {
            if (securityProviders.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.security_providers, ""))
                addAll(securityProviders)
            }
        }
    }

    private fun getGsfAndroidId(): ItemValue? {
        val uri = Uri.parse("content://com.google.android.gsf.gservices")
        val idKey = "android_id"
        val params = arrayOf(idKey)
        return try {
            contentResolver.query(uri, null, null, params, null)?.use {
                it.moveToFirst()
                val hexId = java.lang.Long.toHexString(it.getString(1).toLong())
                ItemValue.NameResource(Res.string.google_services_framework_id, hexId)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getStrongBoxData(): ItemValue {
        val hasStrongBox = if (Build.VERSION.SDK_INT >= 28) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
        } else {
            false
        }
        return ItemValue.NameValueResource(
            Res.string.strongbox, ResourceUtils.getYesNoStringResource(hasStrongBox)
        )
    }

    companion object {
        private const val ENCRYPTION_STATUS_UNSUPPORTED = "UNSUPPORTED"
        private const val ENCRYPTION_STATUS_INACTIVE = "INACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVATING = "ACTIVATING"
        private const val ENCRYPTION_STATUS_ACTIVE = "ACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVE_PER_USER = "ACTIVE_PER_USER"
        private const val ENCRYPTION_STATUS_UNKNOWN = "UNKNOWN"
    }
}
