package com.kgurgul.cpuinfo.data.provider

import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

@Factory
actual class ApplicationsDataProvider actual constructor() : KoinComponent {

    private val packageManager: PackageManager by inject()

    actual fun getInstalledApplications(withSystemApps: Boolean): List<ExtendedApplicationData> {
        val applications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        return if (withSystemApps) {
            applications
        } else {
            applications.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        }.map {
            ExtendedApplicationData(
                it.loadLabel(packageManager).toString(),
                it.packageName,
                it.sourceDir,
                getNativeLibs(it.nativeLibraryDir),
                it.hasNativeLibs(),
                getAppIconUri(it.packageName)
            )
        }
    }

    actual fun areApplicationsSupported() = true

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
        val packageInfo: PackageInfo
        try {
            packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return 0
        }
        return packageInfo.applicationInfo.icon
    }

    private fun getNativeLibs(nativeLibsDir: String?): List<String> {
        if (nativeLibsDir == null) {
            return emptyList()
        }
        val nativeDirFile = File(nativeLibsDir)
        return nativeDirFile.listFiles()?.map { it.name } ?: emptyList()
    }
}