package com.kgurgul.cpuinfo.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            desktopModule,
            sharedModule,
        )
    }
}