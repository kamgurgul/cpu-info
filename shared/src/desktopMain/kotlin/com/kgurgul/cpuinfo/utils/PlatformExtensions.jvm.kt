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

import java.text.Collator
import java.text.Normalizer
import okio.Path.Companion.toPath

actual fun smartCompare(a: String, b: String): Int {
    val collator = Collator.getInstance().apply { strength = Collator.SECONDARY }
    return collator.compare(a.lowercase(), b.lowercase())
}

actual fun String.normalize(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
}

fun getAppConfigPath(appName: String): String {
    val os = System.getProperty("os.name").lowercase()
    val appNamePath = appName.toPath()
    val configPath =
        when {
            os.startsWith("windows") -> {
                val dir = getLocalAppDataPath() ?: "/".toPath()
                dir / appNamePath
            }

            os.contains("os x") -> {
                getHomePath() / "Library/Preferences".toPath() / appNamePath
            }

            else -> {
                val dir = getXdgConfigPath() ?: (getHomePath() / ".config".toPath())
                dir / appNamePath
            }
        }
    return configPath.toString()
}

private fun getHomePath() = System.getProperty("user.home").toPath()

private fun getXdgConfigPath() = System.getenv("XDG_CONFIG_HOME")?.toPath()

private fun getLocalAppDataPath() = System.getenv("LOCALAPPDATA")?.toPath()
