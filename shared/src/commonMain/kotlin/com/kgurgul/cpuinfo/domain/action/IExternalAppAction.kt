package com.kgurgul.cpuinfo.domain.action

interface IExternalAppAction {

    fun launch(packageName: String): Result<Unit>

    fun openSettings(packageName: String): Result<Unit>

    fun uninstall(packageName: String): Result<Unit>

    fun searchOnWeb(phrase: String): Result<Unit>
}
