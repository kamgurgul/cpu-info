package com.kgurgul.cpuinfo.data.provider

expect class PackageNameProvider() : IPackageNameProvider {

    override suspend fun getPackageName(): String
}
