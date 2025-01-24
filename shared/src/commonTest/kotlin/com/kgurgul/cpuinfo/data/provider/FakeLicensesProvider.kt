package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.model.LicenseData

class FakeLicensesProvider : ILicensesProvider {

    override suspend fun getAll(): List<LicenseData> {
        return TestData.licenses
    }
}
