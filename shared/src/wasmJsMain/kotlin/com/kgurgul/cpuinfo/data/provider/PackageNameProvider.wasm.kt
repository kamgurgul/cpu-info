package com.kgurgul.cpuinfo.data.provider

actual class PackageNameProvider actual constructor() : IPackageNameProvider {

    actual override suspend fun getPackageName(): String {
        return "cpuinfo.kgurgul.com"
    }
}
