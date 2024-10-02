package com.kgurgul.cpuinfo.data.provider

class FakeOsDataProvider : IOsDataProvider {

    override suspend fun getData(): List<Pair<String, String>> {
        return listOf("OS" to "Test OS")
    }
}
