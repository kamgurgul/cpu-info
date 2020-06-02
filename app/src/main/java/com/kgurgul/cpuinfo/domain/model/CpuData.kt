package com.kgurgul.cpuinfo.domain.model

data class CpuData(
        val abi: String,
        val coreNumber: Int,
        val frequencies: List<Frequency>
) {

    data class Frequency(
            val min: Long,
            val max: Long,
            val current: Long
    )
}