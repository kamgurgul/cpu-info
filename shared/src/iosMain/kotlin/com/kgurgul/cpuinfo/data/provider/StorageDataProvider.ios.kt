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
import com.kgurgul.cpuinfo.shared.internal
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class StorageDataProvider actual constructor() : IStorageDataProvider, KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual override suspend fun getStorageInfo(): List<StorageItem> {
        return listOfNotNull(getInternalStorage())
    }

    private fun getInternalStorage(): StorageItem? {
        val storageTotal = iosHardwareDataProvider.getTotalDiskSpaceInBytes()
        val availableSpace = iosHardwareDataProvider.getFreeDiskSpaceInBytes()
        val storageUsed = storageTotal - availableSpace
        return if (storageTotal > 0) {
            StorageItem(
                id = "0",
                label = TextResource.Resource(Res.string.internal),
                iconDrawable = Res.drawable.ic_hard_drive,
                storageTotal = storageTotal,
                storageUsed = storageUsed,
            )
        } else {
            null
        }
    }
}
