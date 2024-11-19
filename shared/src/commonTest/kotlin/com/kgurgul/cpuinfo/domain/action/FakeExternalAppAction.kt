package com.kgurgul.cpuinfo.domain.action

class FakeExternalAppAction : IExternalAppAction {

    var isLaunchSuccess = true

    var isLaunchCalled = false
        private set
    var isOpenSettingsCalled = false
        private set
    var isUninstallCalled = false
        private set
    var isSearchOnWebCalled = false
        private set

    override fun launch(packageName: String): Result<Unit> {
        isLaunchCalled = true
        return if (isLaunchSuccess) Result.success(Unit) else Result.failure(Exception())
    }

    override fun openSettings(packageName: String): Result<Unit> {
        isOpenSettingsCalled = true
        return Result.success(Unit)
    }

    override fun uninstall(packageName: String): Result<Unit> {
        isUninstallCalled = true
        return Result.success(Unit)
    }

    override fun searchOnWeb(phrase: String): Result<Unit> {
        isSearchOnWebCalled = true
        return Result.success(Unit)
    }

    fun reset() {
        isLaunchSuccess = true

        isLaunchCalled = false
        isOpenSettingsCalled = false
        isUninstallCalled = false
        isSearchOnWebCalled = false
    }
}
