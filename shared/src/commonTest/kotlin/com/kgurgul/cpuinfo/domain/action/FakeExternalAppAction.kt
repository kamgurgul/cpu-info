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
package com.kgurgul.cpuinfo.domain.action

class FakeExternalAppAction : IExternalAppAction {

    var isLaunchSuccess = true

    var isLaunchCalled = false
        private set

    var isOpenSettingsCalled = false
        private set

    var isUninstallCalled = false
        private set

    var isUninstallWithPathCalled = false
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

    override fun uninstallWithPath(uninstallerPath: String): Result<Unit> {
        isUninstallWithPathCalled = true
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
        isUninstallWithPathCalled = false
        isSearchOnWebCalled = false
    }
}
