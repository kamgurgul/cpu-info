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

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
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
import com.kgurgul.cpuinfo.shared.os_base_os
import com.kgurgul.cpuinfo.shared.os_build_fingerprint
import com.kgurgul.cpuinfo.shared.os_build_number
import com.kgurgul.cpuinfo.shared.os_build_type
import com.kgurgul.cpuinfo.shared.os_language
import com.kgurgul.cpuinfo.shared.os_media_performance_class
import com.kgurgul.cpuinfo.shared.os_play_system_update
import com.kgurgul.cpuinfo.shared.os_seamless_updates
import com.kgurgul.cpuinfo.shared.os_security_patch
import com.kgurgul.cpuinfo.shared.os_system_apps
import com.kgurgul.cpuinfo.shared.os_system_features
import com.kgurgul.cpuinfo.shared.os_system_uptime
import com.kgurgul.cpuinfo.shared.os_time_zone
import com.kgurgul.cpuinfo.shared.os_treble
import com.kgurgul.cpuinfo.shared.os_user_apps
import com.kgurgul.cpuinfo.shared.rooted
import com.kgurgul.cpuinfo.shared.sdk
import com.kgurgul.cpuinfo.shared.security_providers
import com.kgurgul.cpuinfo.shared.serial
import com.kgurgul.cpuinfo.shared.strongbox
import com.kgurgul.cpuinfo.shared.tab_os
import com.kgurgul.cpuinfo.shared.version
import com.kgurgul.cpuinfo.shared.vm
import com.kgurgul.cpuinfo.utils.ResourceUtils
import com.kgurgul.cpuinfo.utils.Utils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.Security
import java.util.Locale
import java.util.TimeZone
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class OsDataProvider actual constructor() : IOsDataProvider, KoinComponent {

    private val contentResolver: ContentResolver by inject()
    private val packageManager: PackageManager by inject()
    private val devicePolicyManager: DevicePolicyManager by inject()

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.tab_os, "Android"))
            addAll(getBuildData())
            addAll(getSystemPropertiesData())
            getPlaySystemUpdateData()?.let { add(it) }
            getMediaPerformanceClassData()?.let { add(it) }
            add(ItemValue.NameResource(Res.string.os_language, Locale.getDefault().displayName))
            add(ItemValue.NameResource(Res.string.os_time_zone, TimeZone.getDefault().id))
            add(
                ItemValue.NameResource(
                    Res.string.os_system_uptime,
                    Utils.formatUptime(SystemClock.elapsedRealtime() / 1000),
                )
            )
            addAll(getApplicationsCountData())
            getAndroidIdData()?.let { add(it) }
            getGsfAndroidId()?.let { add(it) }
            add(
                ItemValue.NameValueResource(
                    Res.string.rooted,
                    ResourceUtils.getYesNoStringResource(isDeviceRooted()),
                )
            )
            getDeviceEncryptionStatus()?.let { add(it) }
            add(getStrongBoxData())
            addAll(getSecurityData())
            getSystemFeaturesData()?.let { add(it) }
        }
    }

    /** Retrieve data from static Build class and system property "java.vm.version" */
    @SuppressLint("HardwareIds")
    private fun getBuildData(): List<ItemValue> {
        return buildList {
            add(ItemValue.NameResource(Res.string.version, Build.VERSION.RELEASE))
            add(ItemValue.NameResource(Res.string.sdk, Build.VERSION.SDK_INT.toString()))
            add(ItemValue.NameResource(Res.string.os_security_patch, Build.VERSION.SECURITY_PATCH))
            add(ItemValue.NameResource(Res.string.codename, Build.VERSION.CODENAME))
            if (Build.VERSION.BASE_OS.isNotBlank()) {
                add(ItemValue.NameResource(Res.string.os_base_os, Build.VERSION.BASE_OS))
            }
            add(ItemValue.NameResource(Res.string.os_build_number, Build.DISPLAY))
            add(ItemValue.NameResource(Res.string.os_build_fingerprint, Build.FINGERPRINT))
            add(ItemValue.NameResource(Res.string.os_build_type, Build.TYPE))
            add(ItemValue.NameResource(Res.string.bootloader, Build.BOOTLOADER))
            add(ItemValue.NameResource(Res.string.brand, Build.BRAND))
            add(ItemValue.NameResource(Res.string.model, Build.MODEL))
            add(ItemValue.NameResource(Res.string.manufacturer, Build.MANUFACTURER))
            add(ItemValue.NameResource(Res.string.board, Build.BOARD))
            add(ItemValue.NameResource(Res.string.vm, getVmVersion()))
            add(ItemValue.NameResource(Res.string.kernel, (System.getProperty("os.version") ?: "")))
            @Suppress("DEPRECATION") add(ItemValue.NameResource(Res.string.serial, Build.SERIAL))
        }
    }

    /** Get AndroidID. Keep in mind that from Android O it is unique per app. */
    @SuppressLint("HardwareIds")
    private fun getAndroidIdData(): ItemValue? {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return if (androidId != null) {
            ItemValue.NameResource(Res.string.android_id, androidId)
        } else {
            null
        }
    }

    /** Add information about device encrypted storage status */
    @Suppress("DEPRECATION")
    private fun getDeviceEncryptionStatus(): ItemValue? {
        return try {
            val statusText =
                when (devicePolicyManager.storageEncryptionStatus) {
                    DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED ->
                        ENCRYPTION_STATUS_UNSUPPORTED
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
        val paths =
            arrayOf(
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
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

    /** Specify if device is using ART or Dalvik */
    private fun getVmVersion(): String {
        var vm = "Dalvik"
        val vmVersion = System.getProperty("java.vm.version")
        if (vmVersion != null && vmVersion.startsWith("2")) {
            vm = "ART"
        }
        return vm
    }

    /** Get information about security providers */
    private fun getSecurityData(): List<ItemValue> {
        val securityProviders =
            Security.getProviders().map { ItemValue.Text(it.name, it.version.toString()) }
        return buildList {
            if (securityProviders.isNotEmpty()) {
                add(ItemValue.NameResource(Res.string.security_providers, ""))
                addAll(securityProviders)
            }
        }
    }

    /** Get Project Treble and seamless (A/B) updates support from system properties */
    private fun getSystemPropertiesData(): List<ItemValue> {
        return buildList {
            getSystemProperty(PROP_TREBLE_ENABLED)?.let {
                add(
                    ItemValue.NameValueResource(
                        Res.string.os_treble,
                        ResourceUtils.getYesNoStringResource(it == "true"),
                    )
                )
            }
            getSystemProperty(PROP_AB_UPDATE)?.let {
                add(
                    ItemValue.NameValueResource(
                        Res.string.os_seamless_updates,
                        ResourceUtils.getYesNoStringResource(it == "true"),
                    )
                )
            }
        }
    }

    private fun getSystemProperty(key: String): String? {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("getprop", key))
            BufferedReader(InputStreamReader(process.inputStream)).use { it.readLine()?.trim() }
        } catch (t: Throwable) {
            null
        } finally {
            process?.destroy()
        }
    }

    /** Get Google Play system update (mainline modules) version */
    private fun getPlaySystemUpdateData(): ItemValue? {
        if (Build.VERSION.SDK_INT < 29) {
            return null
        }
        val version = MODULE_METADATA_PACKAGES.firstNotNullOfOrNull { packageName ->
            try {
                packageManager.getPackageInfo(packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
        return version?.let { ItemValue.NameResource(Res.string.os_play_system_update, it) }
    }

    private fun getMediaPerformanceClassData(): ItemValue? {
        if (Build.VERSION.SDK_INT < 31) {
            return null
        }
        val performanceClass = Build.VERSION.MEDIA_PERFORMANCE_CLASS
        return if (performanceClass > 0) {
            ItemValue.NameResource(
                Res.string.os_media_performance_class,
                performanceClass.toString(),
            )
        } else {
            null
        }
    }

    /** Get amount of installed user and system applications */
    @Suppress("DEPRECATION")
    private fun getApplicationsCountData(): List<ItemValue> {
        return try {
            val (systemApps, userApps) =
                packageManager.getInstalledApplications(0).partition {
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                }
            listOf(
                ItemValue.NameResource(Res.string.os_user_apps, userApps.size.toString()),
                ItemValue.NameResource(Res.string.os_system_apps, systemApps.size.toString()),
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Get list of system features supported by the device */
    private fun getSystemFeaturesData(): ItemValue? {
        val features = packageManager.systemAvailableFeatures.mapNotNull { it.name }.sorted()
        return if (features.isNotEmpty()) {
            ItemValue.Expandable(
                header =
                    ItemValue.FormattedNameResource(
                        Res.string.os_system_features,
                        listOf(features.size),
                        "",
                    ),
                items = features.map { ItemValue.Text(it, "") },
            )
        } else {
            null
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
        val hasStrongBox =
            if (Build.VERSION.SDK_INT >= 28) {
                packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
            } else {
                false
            }
        return ItemValue.NameValueResource(
            Res.string.strongbox,
            ResourceUtils.getYesNoStringResource(hasStrongBox),
        )
    }

    companion object {
        private const val ENCRYPTION_STATUS_UNSUPPORTED = "UNSUPPORTED"
        private const val ENCRYPTION_STATUS_INACTIVE = "INACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVATING = "ACTIVATING"
        private const val ENCRYPTION_STATUS_ACTIVE = "ACTIVE"
        private const val ENCRYPTION_STATUS_ACTIVE_PER_USER = "ACTIVE_PER_USER"
        private const val ENCRYPTION_STATUS_UNKNOWN = "UNKNOWN"
        private const val PROP_TREBLE_ENABLED = "ro.treble.enabled"
        private const val PROP_AB_UPDATE = "ro.build.ab_update"
        private val MODULE_METADATA_PACKAGES =
            listOf("com.google.android.modulemetadata", "com.android.modulemetadata")
    }
}
