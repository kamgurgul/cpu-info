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
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    actual override fun uninstall(packageName: String): Result<Unit> {
        val uri = Uri.fromParts("package", packageName, null)
        val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(uninstallIntent)
        }
    }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        val uri = Uri.parse("http://www.google.com/search?q=$phrase")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return runCatching {
            context.startActivity(intent)
        }
    }
}
