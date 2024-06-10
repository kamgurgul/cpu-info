package com.kgurgul.cpuinfo.domain.model

data class CpuData(
    val processorName: String,
    val abi: String,
    val coreNumber: Int,
    val hasArmNeon: Boolean,
    val frequencies: List<Frequency>,
    val l1dCaches: String,
    val l1iCaches: String,
    val l2Caches: String,
    val l3Caches: String,
    val l4Caches: String
) {

    data class Frequency(
        val min: Long,
        val max: Long,
        val current: Long
    )
}