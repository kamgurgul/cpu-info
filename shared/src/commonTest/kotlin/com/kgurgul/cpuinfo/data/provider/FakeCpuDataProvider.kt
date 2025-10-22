package com.kgurgul.cpuinfo.data.provider

class FakeCpuDataProvider : ICpuDataProvider {

    override fun getAbi(): String {
        return "x64"
    }

    override fun getNumberOfLogicalCores(): Int {
        return 1
    }

    override fun getNumberOfPhysicalCores(): Int {
        return 1
    }

    override fun getCurrentFreq(coreNumber: Int): Long {
        return -1
    }

    override fun getMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        return Pair(-1, -1)
    }
}
