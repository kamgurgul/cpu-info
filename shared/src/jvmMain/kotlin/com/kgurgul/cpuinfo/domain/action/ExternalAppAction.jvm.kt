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

import com.kgurgul.cpuinfo.domain.model.UninstallOutcome
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.uninstall.WindowsUninstaller
import com.kgurgul.cpuinfo.utils.uninstall.trashMacBundle
import com.sun.jna.Platform
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ExternalAppAction actual constructor() : IExternalAppAction, KoinComponent {

    private val dispatchersProvider: IDispatchersProvider by inject()

    actual override fun launch(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun openSettings(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override fun uninstall(packageName: String): Result<Unit> {
        return Result.success(Unit)
    }

    actual override suspend fun uninstallDesktop(
        target: UninstallTarget
    ): Result<UninstallOutcome> =
        withContext(dispatchersProvider.io) {
            try {
                val outcome =
                    when (target) {
                        is UninstallTarget.WindowsRegistry -> {
                            check(Platform.isWindows()) { "Windows removal is unavailable" }
                            WindowsUninstaller().uninstall(target)
                        }

                        is UninstallTarget.MacBundle -> {
                            check(Platform.isMac()) { "macOS removal is unavailable" }
                            trashMacBundle(target)
                        }
                    }
                Result.success(outcome)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}
