package com.kgurgul.cpuinfo.data

import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.local.UserPreferences
import com.kgurgul.cpuinfo.domain.model.ExtendedApplicationData
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
}