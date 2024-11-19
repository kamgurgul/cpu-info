package com.kgurgul.cpuinfo.data.provider

import org.koin.core.component.KoinComponent

actual class PackageNameProvider actual constructor() : IPackageNameProvider, KoinComponent {

    actual override suspend fun getPackageName(): String {
        return "com.kgurgul.cpuinfo"
    }
}
