package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.data.local.UserPreferences
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.ic_cpu_temp

object TestData {

    val userPreferences = UserPreferences(
        isApplicationsSortingAscending = true,
        withSystemApps = false,
        temperatureUnit = 0,
        theme = DarkThemeConfig.FOLLOW_SYSTEM.prefName,
    )

    val extendedApplicationsData = listOf(
        ExtendedApplicationData(
            name = "App1",
            packageName = "com.app1",
            sourceDir = "/data/app1",
            nativeLibs = listOf("/data/app1/lib"),
            hasNativeLibs = true,
            appIconUri = "uri",
        ),
        ExtendedApplicationData(
            name = "App2",
            packageName = "com.app2",
            sourceDir = "/data/app2",
            nativeLibs = listOf("/data/app2/lib"),
            hasNativeLibs = false,
            appIconUri = "uri",
        ),
        ExtendedApplicationData(
            name = "App3",
            packageName = "com.app3",
            sourceDir = "/data/app3",
            nativeLibs = listOf("/data/app3/lib"),
            hasNativeLibs = true,
            appIconUri = "uri",
        ),
    )

    val temperatureData = listOf(
        TemperatureItem(
            id = -1,
            icon = Res.drawable.ic_cpu_temp,
            name = "CPU",
            temperature = 10f
        )
    )

    val processes = listOf(
        ProcessItem(
            name = "name",
            pid = "pid",
            ppid = "ppid",
            niceness = "niceness",
            user = "user",
            rss = "rss",
            vsize = "vsize",
        )
    )

    val cpuData = CpuData(
        processorName = "processorName",
        abi = "abi",
        coreNumber = 1,
        hasArmNeon = true,
        frequencies = listOf(
            CpuData.Frequency(
                min = 1,
                max = 2,
                current = 3
            )
        ),
        l1dCaches = "l1dCaches",
        l1iCaches = "l1iCaches",
        l2Caches = "l2Caches",
        l3Caches = "l3Caches",
        l4Caches = "l4Caches"
    )

    val ramData = RamData(
        total = 100,
        available = 50,
        availablePercentage = 50,
        threshold = 50,
    )

    val gpuData = listOf(
        "vulkanVersion" to "vulkanVersion",
        "glesVersion" to "glEsVersion",
        "metalVersion" to "metalVersion",
        "glVendor" to "glVendor",
        "glRenderer" to "glRenderer",
        "glExtensions" to "glExtensions",
    )

    val storageData = listOf(
        StorageItem(
            id = "0",
            label = "Internal",
            iconDrawable = Res.drawable.baseline_folder_special_24,
            storageTotal = 100,
            storageUsed = 50,
        )
    )

    val itemRowData = listOf(
        "test" to "",
        "test" to "test",
    )
}