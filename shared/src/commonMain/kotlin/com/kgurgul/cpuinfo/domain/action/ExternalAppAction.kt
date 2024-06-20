package com.kgurgul.cpuinfo.domain.action

import org.koin.core.annotation.Factory

@Factory
expect class ExternalAppAction() {

    fun launch(packageName: String): Result<Unit>

    fun openSettings(packageName: String): Result<Unit>

    fun uninstall(packageName: String): Result<Unit>

    fun searchOnWeb(phrase: String): Result<Unit>
}