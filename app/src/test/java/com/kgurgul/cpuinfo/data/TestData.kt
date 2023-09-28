package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.local.UserPreferences
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
import com.kgurgul.cpuinfo.domain.model.GpuData
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.domain.model.RamData
import com.kgurgul.cpuinfo.domain.model.TemperatureItem
import org.mockito.kotlin.mock

object TestData {

    val userPreferences = UserPreferences(
        isApplicationsSortingAscending = true,
        withSystemApps = false
    )

    val extendedApplicationsData = listOf(
        ExtendedApplicationData(
            name = "App1",
            packageName = "com.app1",
            sourceDir = "/data/app1",
            nativeLibraryDir = "/data/app1/lib",
            hasNativeLibs = true,
            appIconUri = mock(),
        ),
        ExtendedApplicationData(
            name = "App2",
            packageName = "com.app2",
            sourceDir = "/data/app2",
            nativeLibraryDir = "/data/app2/lib",
            hasNativeLibs = false,
            appIconUri = mock(),
        ),
        ExtendedApplicationData(
            name = "App3",
            packageName = "com.app3",
            sourceDir = "/data/app3",
            nativeLibraryDir = "/data/app3/lib",
            hasNativeLibs = true,
            appIconUri = mock(),
        ),
    )

    val temperatureData = listOf(
        TemperatureItem(
            iconRes = R.drawable.ic_cpu_temp,
            nameRes = R.string.cpu,
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

    val gpuData = GpuData(
        vulkanVersion = "vulkanVersion",
        glesVersion = "glEsVersion",
        glVendor = "glVendor",
        glRenderer = "glRenderer",
        glExtensions = "glExtensions",
    )
}