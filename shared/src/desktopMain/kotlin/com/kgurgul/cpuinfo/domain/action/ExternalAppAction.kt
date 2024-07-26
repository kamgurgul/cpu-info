package com.kgurgul.cpuinfo.domain.action

import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
actual class ExternalAppAction actual constructor() : KoinComponent {

    actual fun launch(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun openSettings(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun uninstall(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}