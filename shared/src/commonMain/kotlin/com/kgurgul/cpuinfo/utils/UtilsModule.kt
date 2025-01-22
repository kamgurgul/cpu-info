package com.kgurgul.cpuinfo.utils

import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val utilsModule = module {
    singleOf(::DefaultDispatchersProvider) bind IDispatchersProvider::class
    single {
        Json {
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            ignoreUnknownKeys = true
        }
    }
}
