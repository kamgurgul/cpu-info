/*
 * Copyright 2017 KG Soft
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

@file:JvmName("Extensions")

package com.kgurgul.cpuinfo.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kgurgul.cpuinfo.BuildConfig
import com.kgurgul.cpuinfo.R

/**
 * All basic extensions
 *
 * @author kgurgul
 */
fun Float.round1(): Float = Math.round(this * 10.0) / 10.0f

fun Double.round1(): Double = Math.round(this * 10.0) / 10.0

fun Float.round2(): Float = Math.round(this * 100.0) / 100.0f

fun Double.round2(): Double = Math.round(this * 100.0) / 100.0

inline fun runOnApi(api: Int, f: () -> Unit, otherwise: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT == api) {
        f()
    } else {
        otherwise()
    }
}

inline fun runOnApiBelow(api: Int, f: () -> Unit) {
    if (Build.VERSION.SDK_INT < api) {
        f()
    }
}

inline fun runOnApiAbove(api: Int, f: () -> Unit) {
    if (Build.VERSION.SDK_INT > api) {
        f()
    }
}

inline fun runOnApiBelow(api: Int, f: () -> Unit, otherwise: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT < api) {
        f()
    } else {
        otherwise()
    }
}

inline fun runOnApiAbove(api: Int, f: () -> Unit, otherwise: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT > api) {
        f()
    } else {
        otherwise()
    }
}

/**
 * @return true for Debug build, otherwise false
 */
fun isDebugBuild(): Boolean = BuildConfig.DEBUG

/**
 * @return true if used device is tablet
 */
fun Context.isTablet(): Boolean = this.resources.getBoolean(R.bool.isTablet)

@TargetApi(19)
fun Fragment.createSafFile(mimeType: String, fileName: String, requestCode: Int) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = mimeType
        putExtra(Intent.EXTRA_TITLE, fileName)
    }
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        Toast.makeText(context, R.string.action_not_supported, Toast.LENGTH_SHORT).show()
    }
}
