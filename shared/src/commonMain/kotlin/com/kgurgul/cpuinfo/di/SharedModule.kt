package com.kgurgul.cpuinfo.di

import com.kgurgul.cpuinfo.components.componentsModule
import com.kgurgul.cpuinfo.data.dataModule
import com.kgurgul.cpuinfo.domain.domainModule
import com.kgurgul.cpuinfo.features.featuresModule
import com.kgurgul.cpuinfo.utils.utilsModule
import org.koin.dsl.module

val sharedModule = module {
    includes(
        componentsModule,
        dataModule,
        domainModule,
        featuresModule,
        utilsModule,
        viewModelModule,
    )
}
