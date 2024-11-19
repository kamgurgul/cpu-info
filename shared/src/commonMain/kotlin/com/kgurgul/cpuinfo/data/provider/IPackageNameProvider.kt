package com.kgurgul.cpuinfo.data.provider

interface IPackageNameProvider {

    suspend fun getPackageName(): String
}
