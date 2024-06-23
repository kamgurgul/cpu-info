package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
actual class PackageNameProvider actual constructor() {

    actual suspend fun getPackageName(): String {
        return "com.kgurgul.cpuinfo"
    }
}