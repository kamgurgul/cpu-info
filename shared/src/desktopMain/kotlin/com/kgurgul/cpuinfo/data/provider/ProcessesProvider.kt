/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo
import oshi.software.os.OperatingSystem.ProcessSorting

@Factory
actual class ProcessesProvider actual constructor() : KoinComponent {

    private val systemInfo: SystemInfo by inject()
    private val operatingSystem = systemInfo.operatingSystem

    actual fun areProcessesSupported() = true

    actual fun getProcessList(): List<ProcessItem> {
        return operatingSystem.getProcesses(null, ProcessSorting.NAME_ASC, 0).map {
            ProcessItem(
                name = it.name,
                pid = it.processID.toString(),
                ppid = it.parentProcessID.toString(),
                niceness = it.priority.toString(),
                user = it.user,
                rss = it.residentSetSize.toString(),
                vsize = it.virtualSize.toString()
            )
        }
    }
}