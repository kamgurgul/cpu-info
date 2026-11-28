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
        return systemInfo.operatingSystem.fileSystem.fileStores
            .filter { it.totalSpace > 0 }
            .map { fileStore ->
                val label =
                    fileStoreLabel(
                        label = fileStore.label,
                        name = fileStore.name,
                        type = fileStore.type,
                        mount = fileStore.mount,
                    )
                label to
                    StorageItem(
                        id = fileStore.mount.ifBlank { fileStore.uuid },
                        label = TextResource.Text(label),
                        iconDrawable = Res.drawable.ic_hard_drive,
                        storageTotal = fileStore.totalSpace,
                        storageUsed = fileStore.totalSpace - fileStore.usableSpace,
                    )
            }
            .sortedBy { (label, _) -> label }
            .map { (_, storageItem) -> storageItem }
            .distinctBy { it.id }
    }
}

/**
 * Builds a label with the volume name in the first line and the remaining details in the second
 * one. OSHI often reports the same value for several fields (e.g. label and name on macOS), so
 * duplicates are skipped.
 */
internal fun fileStoreLabel(label: String?, name: String?, type: String?, mount: String?): String {
    val title = label?.trim()?.takeIf { it.isNotEmpty() } ?: name?.trim().orEmpty()
    val details =
        listOf(name, type, mount)
            .mapNotNull { value -> value?.trim()?.takeIf { it.isNotEmpty() && it != title } }
            .distinct()
            .joinToString(separator = " • ")
    return listOf(title, details).filter { it.isNotEmpty() }.joinToString(separator = "\n")
}
