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
package com.kgurgul.cpuinfo.domain.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ExternalAppAction actual constructor() : IExternalAppAction, KoinComponent {

    private val context: Context by inject()

    actual override fun launch(packageName: String): Result<Unit> {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            try {
                context.startActivity(intent)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("Intent is null"))
        }
    }

    actual override fun openSettings(packageName: String): Result<Unit> {
        val uri = Uri.fromParts("package", packageName, null)
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        return runCatching { context.startActivity(intent) }
    }

    @Suppress("DEPRECATION")
    actual override fun uninstall(packageName: String): Result<Unit> {
        val uri = Uri.fromParts("package", packageName, null)
        val uninstallIntent =
            Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        return runCatching { context.startActivity(uninstallIntent) }
    }

    actual override fun uninstallWithPath(uninstallerPath: String): Result<Unit> {
        // Android doesn't use path-based uninstallation, use standard uninstall instead
        return Result.success(Unit)
    }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        val uri = Uri.parse("http://www.google.com/search?q=$phrase")
        val intent =
            Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        return runCatching { context.startActivity(intent) }
    }
}
