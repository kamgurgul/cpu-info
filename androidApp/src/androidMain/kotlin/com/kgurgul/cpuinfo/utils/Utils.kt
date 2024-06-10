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

package com.kgurgul.cpuinfo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.kgurgul.cpuinfo.R

/**
 * Open google with passed query
 */
fun Context.searchInGoogle(query: String) {
    val uri = Uri.parse("http://www.google.com/search?q=$query")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, R.string.action_not_supported, Toast.LENGTH_SHORT).show()
    }
}

/**
 * In the feature this method should be replaced with PackageManager
 */
@Suppress("DEPRECATION")
fun Context.uninstallApp(packageName: String) {
    val uri = Uri.fromParts("package", packageName, null)
    val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri)
    startActivity(uninstallIntent)
}
