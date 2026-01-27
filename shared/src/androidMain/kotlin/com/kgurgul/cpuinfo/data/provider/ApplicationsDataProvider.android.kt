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

import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import java.io.File
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ApplicationsDataProvider actual constructor() :
    IApplicationsDataProvider, KoinComponent {

    private val packageManager: PackageManager by inject()

    actual override fun getInstalledApplications(
        withSystemApps: Boolean
    ): List<ExtendedApplicationData> {
        val packages = packageManager.getInstalledPackages(0)
        return if (withSystemApps) {
                packages
            } else {
                packages.filter {
                    val applicationInfo = it.applicationInfo
                    applicationInfo != null &&
                        (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                }
            }
            .map {
                ExtendedApplicationData(
                    name = it.applicationInfo?.loadLabel(packageManager)?.toString() ?: "",
                    packageName = it.packageName,
                    versionName = it.versionName ?: "N/A",
                    nativeLibs = getNativeLibs(it.applicationInfo?.nativeLibraryDir),
                    hasNativeLibs = it.applicationInfo?.hasNativeLibs() ?: false,
                    appIconUri = getAppIconUri(it.packageName),
                )
            }
    }

    actual override fun areApplicationsSupported() = true

    actual override fun hasSystemAppsFiltering() = true

    actual override fun hasExpandedAppManagementSupported() = true

    actual override fun hasManualRefresh() = false

    private fun ApplicationInfo.hasNativeLibs(): Boolean {
        return if (nativeLibraryDir != null) {
            val fileDir = File(nativeLibraryDir)
            val list = fileDir.listFiles()
            list != null && list.isNotEmpty()
        } else {
            false
        }
    }

    private fun getAppIconUri(packageName: String): String {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName)
            .path(getResourceId(packageName).toString())
            .build()
            .toString()
    }

    private fun getResourceId(packageName: String): Int {
        return try {
            val packageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(0),
                    )
                } else {
                    packageManager.getPackageInfo(packageName, 0)
                }
            packageInfo.applicationInfo?.icon ?: 0
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

    private fun getNativeLibs(nativeLibsDir: String?): List<String> {
        if (nativeLibsDir == null) {
            return emptyList()
        }
        val nativeDirFile = File(nativeLibsDir)
        return nativeDirFile.listFiles()?.map { it.name } ?: emptyList()
    }
}
