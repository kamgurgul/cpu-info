package com.kgurgul.cpuinfo.utils

import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.no
import com.kgurgul.cpuinfo.shared.yes

object ResourceUtils {

    fun getYesNoStringResource(yesValue: Boolean) = if (yesValue) {
        Res.string.yes
    } else {
        Res.string.no
    }
}
