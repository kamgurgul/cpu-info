package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.data.local.model.UserPreferences
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.DarkThemeConfig
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.domain.model.StorageItem
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.baseline_folder_special_24
import com.kgurgul.cpuinfo.shared.cpu
import com.kgurgul.cpuinfo.shared.ic_cpu_temp
import kotlinx.collections.immutable.persistentListOf

object TestData {

    val userPreferences = UserPreferences(
        isApplicationsSortingAscending = true,
        isProcessesSortingAscending = true,
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
            id = -2,
            icon = Res.drawable.ic_cpu_temp,
            name = TextResource.Resource(Res.string.cpu),
            temperature = 10f,
        ),
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
        ),
    )

    val cpuData = CpuData(
        processorName = "CPU_NAME",
        abi = "x64",
        coreNumber = 1,
        hasArmNeon = true,
        frequencies = emptyList(),
        l1dCaches = "",
        l1iCaches = "",
        l2Caches = "",
        l3Caches = "",
        l4Caches = "",
    )

    val ramData = RamData(
        total = 1024L,
        available = 512L,
        availablePercentage = 50,
        threshold = 256L,
    )

    val gpuData = persistentListOf(
        ItemValue.Text("vulkanVersion", "vulkanVersion"),
        ItemValue.Text("glesVersion", "glEsVersion"),
        ItemValue.Text("metalVersion", "metalVersion"),
        ItemValue.Text("glVendor", "glVendor"),
        ItemValue.Text("glRenderer", "glRenderer"),
        ItemValue.Text("glExtensions", "glExtensions"),
    )

    val storageData = persistentListOf(
        StorageItem(
            id = "0",
            label = TextResource.Text("Internal"),
            iconDrawable = Res.drawable.baseline_folder_special_24,
            storageTotal = 100,
            storageUsed = 50,
        ),
    )

    val itemValueRowData = persistentListOf(
        ItemValue.Text("test", ""),
        ItemValue.Text("test", "test"),
    )
}
