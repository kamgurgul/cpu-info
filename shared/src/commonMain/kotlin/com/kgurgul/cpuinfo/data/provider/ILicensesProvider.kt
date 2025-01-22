package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.local.model.LicenseData

interface ILicensesProvider {

    suspend fun getAll(): List<LicenseData>
}
