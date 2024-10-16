package com.kgurgul.cpuinfo.utils

import com.kgurgul.cpuinfo.utils.resources.ILocalResources
import com.kgurgul.cpuinfo.utils.resources.LocalResources
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val utilsModule = module {
    factoryOf(::LocalResources) bind ILocalResources::class

    singleOf(::DefaultDispatchersProvider) bind IDispatchersProvider::class
}
