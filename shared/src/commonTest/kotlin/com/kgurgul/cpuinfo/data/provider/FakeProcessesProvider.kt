package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem

class FakeProcessesProvider(
    var areProcessesSupported: Boolean = false,
    var processesList: List<ProcessItem> = emptyList(),
) : IProcessesProvider {

    override fun areProcessesSupported(): Boolean {
        return areProcessesSupported
    }

    override fun getProcessList(): List<ProcessItem> {
        return processesList
    }
}