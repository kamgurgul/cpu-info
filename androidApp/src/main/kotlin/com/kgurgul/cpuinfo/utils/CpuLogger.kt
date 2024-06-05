package com.kgurgul.cpuinfo.utils

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

object CpuLogger : Logger(
    config = loggerConfigInit(
        platformLogWriter(NoTagFormatter),
        minSeverity = Severity.Info
    ),
    tag = "CpuInfo",
)