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
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import oshi.SystemInfo

actual class StorageDataProvider actual constructor() : IStorageDataProvider, KoinComponent {

    private val systemInfo: SystemInfo by inject()

    actual override suspend fun getStorageInfo(): List<StorageItem> {
        val fileStores = systemInfo.operatingSystem.fileSystem.fileStores
        return buildList {
                fileStores.forEach { osFileStore ->
                    val label = buildString {
                        if (!osFileStore.label.isNullOrEmpty()) {
                            appendLine(osFileStore.label)
                        }
                        appendLine(osFileStore.name)
                        appendLine(osFileStore.type)
                    }
                    add(
                        StorageItem(
                            id = osFileStore.uuid,
                            label = TextResource.Text(label),
                            iconDrawable = Res.drawable.ic_hard_drive,
                            storageTotal = osFileStore.totalSpace,
                            storageUsed = osFileStore.totalSpace - osFileStore.freeSpace,
                        )
                    )
                }
            }
            .distinctBy { it.id }
            .sortedBy { (it.label as? TextResource.Text)?.value }
    }
}
