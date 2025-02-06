package com.kgurgul.cpuinfo.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.kgurgul.cpuinfo.data.provider.IPackageNameProvider
import com.kgurgul.cpuinfo.shared.R
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.applications
import com.kgurgul.cpuinfo.shared.temperature
import com.kgurgul.cpuinfo.utils.navigation.NavigationConst
import org.jetbrains.compose.resources.getString

class AndroidShortcutManager(
    private val context: Context,
    private val packageNameProvider: IPackageNameProvider,
    private val packageManager: PackageManager,
) {

    suspend fun createShortcuts(withApplications: Boolean) {
        try {
            val shortcuts = buildList {
                if (withApplications) {
                    add(
                        ShortcutInfoCompat.Builder(context, NavigationConst.APPLICATIONS)
                            .setShortLabel(getString(Res.string.applications))
                            .setIcon(
                                IconCompat.createWithResource(
                                    context, R.drawable.ic_apps_shortcut
                                )
                            )
                            .setIntent(
                                packageManager.getLaunchIntentForPackage(
                                    packageNameProvider.getPackageName()
                                )!!.setData(
                                    Uri.parse(NavigationConst.BASE_URL + NavigationConst.APPLICATIONS)
                                )
                            )
                            .build()
                    )
                }
                add(
                    ShortcutInfoCompat.Builder(context, NavigationConst.TEMPERATURES)
                        .setShortLabel(getString(Res.string.temperature))
                        .setIcon(
                            IconCompat.createWithResource(
                                context, R.drawable.ic_temp_shortcut
                            )
                        )
                        .setIntent(
                            packageManager.getLaunchIntentForPackage(
                                packageNameProvider.getPackageName()
                            )!!.setData(
                                Uri.parse(NavigationConst.BASE_URL + NavigationConst.TEMPERATURES)
                            )
                        )
                        .build()
                )
            }
            ShortcutManagerCompat.addDynamicShortcuts(context, shortcuts)
        } catch (e: Exception) {
            CpuLogger.e { "Error during creating shortcuts: $e" }
        }
    }
}
