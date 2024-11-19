package com.kgurgul.cpuinfo.data.provider

class FakePackageNameProvider : IPackageNameProvider {

    override suspend fun getPackageName(): String {
        return "com.kgurgul.cpuinfo"
    }
}
