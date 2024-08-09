package com.kgurgul.cpuinfo.features.applications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun registerUninstallListener(onRefresh: () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
        val uninstallReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onRefresh()
            }
        }
        ContextCompat.registerReceiver(
            context, uninstallReceiver, filter, ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(uninstallReceiver)
        }
    }
}