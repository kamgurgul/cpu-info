package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ProcessItem

interface IProcessesProvider {

    fun areProcessesSupported(): Boolean

    fun getProcessList(): List<ProcessItem>
}