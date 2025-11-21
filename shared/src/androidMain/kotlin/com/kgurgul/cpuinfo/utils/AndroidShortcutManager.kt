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
package com.kgurgul.cpuinfo.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
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
                                IconCompat.createWithResource(context, R.drawable.ic_apps_shortcut)
                            )
                            .setIntent(
                                packageManager
                                    .getLaunchIntentForPackage(
                                        packageNameProvider.getPackageName()
                                    )!!
                                    .setData(
                                        (NavigationConst.BASE_URL + NavigationConst.APPLICATIONS)
                                            .toUri()
                                    )
                            )
                            .build()
                    )
                }
                add(
                    ShortcutInfoCompat.Builder(context, NavigationConst.TEMPERATURES)
                        .setShortLabel(getString(Res.string.temperature))
                        .setIcon(
                            IconCompat.createWithResource(context, R.drawable.ic_temp_shortcut)
                        )
                        .setIntent(
                            packageManager
                                .getLaunchIntentForPackage(packageNameProvider.getPackageName())!!
                                .setData(
                                    (NavigationConst.BASE_URL + NavigationConst.TEMPERATURES)
                                        .toUri()
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
