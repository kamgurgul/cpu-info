package com.kgurgul.cpuinfo.domain.model

data class License(
    val moduleName: String,
    val moduleVersion: String,
    val license: String,
    val licenseUrl: String,
)
