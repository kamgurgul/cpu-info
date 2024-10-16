package com.kgurgul.cpuinfo.features

import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModule = module {
    factoryOf(::TemperatureFormatter)
}
