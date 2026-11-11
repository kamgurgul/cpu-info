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

actual class ExternalAppAction actual constructor() : IExternalAppAction {

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
    ): Result<UninstallOutcome> {
        return Result.failure(UnsupportedOperationException("Desktop removal is unavailable"))
    }

    actual override fun searchOnWeb(phrase: String): Result<Unit> {
        return Result.success(Unit)
    }
}
