package com.kgurgul.cpuinfo.domain.observable

import android.content.ContentResolver
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.kgurgul.cpuinfo.data.provider.ApplicationsDataProvider
import com.kgurgul.cpuinfo.domain.MutableInteractor
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.utils.DispatchersProvider
import com.kgurgul.cpuinfo.utils.wrapToResultFlow
import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class ApplicationsDataObservable @Inject constructor(
    dispatchersProvider: DispatchersProvider,
    private val applicationsDataProvider: ApplicationsDataProvider,
    private val packageManager: PackageManager
) : MutableInteractor<Boolean, Result<List<ExtendedApplicationData>>>() {

    override val dispatcher = dispatchersProvider.io

    override fun createObservable(params: Boolean): Flow<Result<List<ExtendedApplicationData>>> {
        return wrapToResultFlow {
            applicationsDataProvider.getInstalledApplications(params).map {
                ExtendedApplicationData(
                    it.loadLabel(packageManager).toString(),
                    it.packageName,
                    it.sourceDir,
                    it.nativeLibraryDir,
                    it.hasNativeLibs(),
                    getAppIconUri(it.packageName)
                )
            }
        }
    }

    private fun ApplicationInfo.hasNativeLibs(): Boolean {
        return if (nativeLibraryDir != null) {
            val fileDir = File(nativeLibraryDir)
            val list = fileDir.listFiles()
            list != null && list.isNotEmpty()
        } else {
            false
        }
    }

    private fun getAppIconUri(packageName: String): Uri {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(packageName)
            .path(getResourceId(packageName).toString())
            .build()
    }

    private fun getResourceId(packageName: String): Int {
        val packageInfo: PackageInfo
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return 0
        }
        return packageInfo.applicationInfo.icon
    }
}