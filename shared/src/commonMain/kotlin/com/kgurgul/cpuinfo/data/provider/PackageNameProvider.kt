package com.kgurgul.cpuinfo.data.provider

expect class PackageNameProvider() {

    suspend fun getPackageName(): String
}
