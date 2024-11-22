package com.kgurgul.cpuinfo.utils

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val utilsModule = module {
    singleOf(::DefaultDispatchersProvider) bind IDispatchersProvider::class
}
