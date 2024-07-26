package com.kgurgul.cpuinfo.di

import com.kgurgul.cpuinfo.data.provider.IosHardwareDataProvider
import com.kgurgul.cpuinfo.data.provider.IosSoftwareDataProvider
import org.koin.core.context.startKoin

fun initKoin(
    iosHardwareDataProvider: IosHardwareDataProvider,
    iosSoftwareDataProvider: IosSoftwareDataProvider,
) {
    startKoin {
        modules(
            iosModule(iosHardwareDataProvider, iosSoftwareDataProvider),
            sharedModule,
        )
    }
}