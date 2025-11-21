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

import com.kgurgul.cpuinfo.domain.model.ItemValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSProcessInfo

actual class RamDataProvider actual constructor() : IRamDataProvider, KoinComponent {

    private val iosHardwareDataProvider: IosHardwareDataProvider by inject()

    actual override fun getTotalBytes(): Long {
        return NSProcessInfo.processInfo().physicalMemory.toLong()
    }

    actual override fun getAvailableBytes(): Long {
        return iosHardwareDataProvider.getAvailableMemory()
    }

    actual override fun getThreshold(): Long {
        return -1L
    }

    actual override fun getAdditionalData(): List<ItemValue> {
        return emptyList()
    }
}
