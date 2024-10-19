package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem

class FakeProcessesProvider(
    var processesSupported: Boolean = false,
    var processesList: List<ProcessItem> = emptyList(),
) : IProcessesProvider {

    override fun areProcessesSupported(): Boolean {
        return processesSupported
    }

    override fun getProcessList(): List<ProcessItem> {
        return processesList
    }
}
