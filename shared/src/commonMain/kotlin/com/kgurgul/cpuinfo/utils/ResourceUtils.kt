package com.kgurgul.cpuinfo.utils

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.yes
import org.jetbrains.compose.resources.getString

object ResourceUtils {

    suspend fun getYesNoString(yesValue: Boolean) = if (yesValue) {
        getString(Res.string.yes)
    } else {
        getString(Res.string.no)
    }
}