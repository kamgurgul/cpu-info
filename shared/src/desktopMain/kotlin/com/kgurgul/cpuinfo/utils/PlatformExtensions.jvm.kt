package com.kgurgul.cpuinfo.utils

import java.text.Collator
import okio.Path.Companion.toPath

actual fun smartCompare(a: String, b: String): Int {
    val collator = Collator.getInstance().apply {
        strength = Collator.SECONDARY
    }
    return collator.compare(a.lowercase(), b.lowercase())
}

fun getAppConfigPath(appName: String): String {
    val os = System.getProperty("os.name").lowercase()
    val appNamePath = appName.toPath()
    val configPath = when {
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
