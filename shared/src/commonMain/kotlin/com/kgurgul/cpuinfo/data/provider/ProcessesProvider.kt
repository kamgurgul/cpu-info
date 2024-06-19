package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem
import org.koin.core.annotation.Factory

@Factory
expect class ProcessesProvider() {

    fun areProcessesSupported(): Boolean

    fun getProcessList(): List<ProcessItem>
}