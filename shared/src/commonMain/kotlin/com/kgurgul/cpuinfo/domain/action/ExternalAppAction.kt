package com.kgurgul.cpuinfo.domain.action

expect class ExternalAppAction() : IExternalAppAction {

    override fun launch(packageName: String): Result<Unit>

    override fun openSettings(packageName: String): Result<Unit>

    override fun uninstall(packageName: String): Result<Unit>

    override fun searchOnWeb(phrase: String): Result<Unit>
}
