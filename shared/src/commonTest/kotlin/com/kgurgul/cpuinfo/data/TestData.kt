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
package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.data.local.model.LicenseData
import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.model.License
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.cpu_abi
import com.kgurgul.cpuinfo.shared.cpu_cores
import com.kgurgul.cpuinfo.shared.cpu_soc_name
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import kotlinx.collections.immutable.persistentListOf

object TestData {

    val userPreferences =
        UserPreferences(
            isApplicationsSortingAscending = true,
            isProcessesSortingAscending = true,
            withSystemApps = false,
            temperatureUnit = 0,
            theme = DarkThemeConfig.FOLLOW_SYSTEM.prefName,
        )

    val extendedApplicationsData =
        listOf(
            ExtendedApplicationData(
                name = "App1",
                packageName = "com.app1",
                versionName = "1.0.0",
                nativeLibs = listOf("/data/app1/lib"),
                hasNativeLibs = true,
                appIconUri = "uri",
            ),
            ExtendedApplicationData(
                name = "App2",
                packageName = "com.app2",
                versionName = "1.0.0",
                nativeLibs = listOf("/data/app2/lib"),
                hasNativeLibs = false,
                appIconUri = "uri",
            ),
            ExtendedApplicationData(
                name = "App3",
                packageName = "com.app3",
                versionName = "1.0.0",
                nativeLibs = listOf("/data/app3/lib"),
                hasNativeLibs = true,
                appIconUri = "uri",
            ),
        )

    val temperatureData =
        listOf(
            TemperatureItem(
                id = -2,
                icon = Res.drawable.ic_cpu_temp,
                name = TextResource.Resource(Res.string.cpu),
                temperature = 10f,
            )
        )

    val processes =
        listOf(
            ProcessItem(
                name = "name",
                pid = "pid",
                ppid = "ppid",
                niceness = "niceness",
                user = "user",
                rss = "rss",
                vsize = "vsize",
            ),
            ProcessItem(
                name = "nazwa",
                pid = "pid1",
                ppid = "ppid1",
                niceness = "niceness1",
                user = "user1",
                rss = "rss1",
                vsize = "vsize1",
            ),
        )

    val cpuData =
        CpuData(
            cpuItems =
                listOf(
                    ItemValue.NameResource(Res.string.cpu_soc_name, "CPU_NAME"),
                    ItemValue.NameResource(Res.string.cpu_abi, "x64"),
                    ItemValue.NameResource(Res.string.cpu_cores, "1"),
                ),
            frequencies = emptyList(),
        )

    val ramData =
        RamData(
            total = 1024L,
            available = 512L,
            availablePercentage = 50,
            threshold = 256L,
            additionalData = emptyList(),
        )

    val gpuData =
        persistentListOf(
            ItemValue.Text("vulkanVersion", "vulkanVersion"),
            ItemValue.Text("glesVersion", "glEsVersion"),
            ItemValue.Text("metalVersion", "metalVersion"),
            ItemValue.Text("glVendor", "glVendor"),
            ItemValue.Text("glRenderer", "glRenderer"),
            ItemValue.Text("glExtensions", "glExtensions"),
        )

    val storageData =
        persistentListOf(
            StorageItem(
                id = "0",
                label = TextResource.Text("Internal"),
                iconDrawable = Res.drawable.baseline_folder_special_24,
                storageTotal = 100,
                storageUsed = 50,
            )
        )

    val itemValueRowData =
        persistentListOf(ItemValue.Text("test", ""), ItemValue.Text("test", "test"))

    val licenseData =
        listOf(
            LicenseData(
                moduleName = "moduleA",
                moduleUrl = "https://test.test",
                moduleVersion = "1.0",
                moduleLicense = "Apache 2.0",
                moduleLicenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
            ),
            LicenseData(
                moduleName = "moduleB",
                moduleUrl = null,
                moduleVersion = "2.0",
                moduleLicense = "MIT",
                moduleLicenseUrl = "https://test2.test",
            ),
        )

    val licenses =
        listOf(
            License(
                moduleName = "moduleA",
                moduleVersion = "1.0",
                license = "Apache 2.0",
                licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0",
            ),
            License(
                moduleName = "moduleB",
                moduleVersion = "2.0",
                license = "MIT",
                licenseUrl = "https://test2.test",
            ),
        )
}
