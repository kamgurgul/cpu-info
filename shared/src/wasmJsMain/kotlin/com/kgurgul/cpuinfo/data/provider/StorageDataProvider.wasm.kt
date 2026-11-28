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
import com.kgurgul.cpuinfo.shared.browser_storage_quota
import com.kgurgul.cpuinfo.shared.ic_hard_drive
import com.kgurgul.cpuinfo.utils.BrowserStorageEstimate
import com.kgurgul.cpuinfo.utils.getStorageEstimate
import kotlinx.coroutines.await

actual class StorageDataProvider actual constructor() : IStorageDataProvider {

    actual override suspend fun getStorageInfo(): List<StorageItem> {
        val estimate = getStorageEstimate().await<BrowserStorageEstimate>()
        return listOf(
            StorageItem(
                id = "0",
                label = TextResource.Resource(Res.string.browser_storage_quota),
                iconDrawable = Res.drawable.ic_hard_drive,
                storageTotal = estimate.quota.toLong(),
                storageUsed = estimate.usage.toLong(),
            )
        )
    }
}
