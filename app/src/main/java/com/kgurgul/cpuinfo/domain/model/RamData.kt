package com.kgurgul.cpuinfo.domain.model

data class RamData(
    val total: Long,
    val available: Long,
    val availablePercentage: Int,
    val threshold: Long
)