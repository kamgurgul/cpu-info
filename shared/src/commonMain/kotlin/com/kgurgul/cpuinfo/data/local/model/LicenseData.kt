package com.kgurgul.cpuinfo.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class DependencyLicenses(
    val dependencies: List<LicenseData>
)

@Serializable
data class LicenseData(
    val moduleName: String,
    val moduleUrl: String?,
    val moduleVersion: String,
    val moduleLicense: String,
    val moduleLicenseUrl: String,
)
