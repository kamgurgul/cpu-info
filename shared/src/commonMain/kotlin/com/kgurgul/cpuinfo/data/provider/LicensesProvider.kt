package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.data.local.model.DependencyLicenses
import com.kgurgul.cpuinfo.data.local.model.LicenseData
import com.kgurgul.cpuinfo.shared.Res
import kotlinx.serialization.json.Json

class LicensesProvider(
    private val json: Json
) : ILicensesProvider {

    override suspend fun getAll(): List<LicenseData> {
        val licensesFileBytes = Res.readBytes("files/licenses.json")
        return json
            .decodeFromString<DependencyLicenses>(licensesFileBytes.decodeToString())
            .dependencies
    }
}
