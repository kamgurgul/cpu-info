package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem

expect class ProcessesProvider() : IProcessesProvider {

    override fun areProcessesSupported(): Boolean

    override fun getProcessList(): List<ProcessItem>
}
