package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.TestData
import com.kgurgul.cpuinfo.data.local.model.LicenseData

class FakeLicensesProvider(
    var throwError: Boolean = false,
) : ILicensesProvider {

    override suspend fun getAll(): List<LicenseData> {
        return if (throwError) {
            throw Exception()
        } else {
            TestData.licenseData
        }
    }

    fun reset() {
        throwError = false
    }
}
