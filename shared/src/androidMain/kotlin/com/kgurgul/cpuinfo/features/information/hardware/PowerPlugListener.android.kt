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
package com.kgurgul.cpuinfo.features.information.hardware

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED

@Composable
actual fun registerPowerPlugListener(onRefresh: () -> Unit) {
    val context = LocalContext.current
    DisposableEffect(context) {
        val filter =
            IntentFilter().apply {
                addAction("android.intent.action.ACTION_POWER_CONNECTED")
                addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
            }
        val powerReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    onRefresh()
                }
            }
        ContextCompat.registerReceiver(context, powerReceiver, filter, RECEIVER_EXPORTED)

        onDispose { context.unregisterReceiver(powerReceiver) }
    }
}
