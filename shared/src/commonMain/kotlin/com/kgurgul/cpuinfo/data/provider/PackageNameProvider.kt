package com.kgurgul.cpuinfo.data.provider

import org.koin.core.annotation.Factory

@Factory
expect class PackageNameProvider() {

    suspend fun getPackageName(): String
}