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

import com.kgurgul.cpuinfo.components.IRamCleanupComponent
import com.kgurgul.cpuinfo.domain.ResultInteractor
import com.kgurgul.cpuinfo.utils.IDispatchersProvider

class RamCleanupAction(
    dispatchersProvider: IDispatchersProvider,
    private val ramCleanupComponent: IRamCleanupComponent,
) : ResultInteractor<Unit, Unit>() {

    override val dispatcher = dispatchersProvider.io

    override suspend fun doWork(params: Unit) {
        ramCleanupComponent.cleanup()
    }

    fun isCleanupActionAvailable(): Boolean {
        return ramCleanupComponent.isCleanupActionAvailable()
    }
}
