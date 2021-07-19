package com.kgurgul.cpuinfo.features.information.cpu

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.cpuFrequency
import com.kgurgul.cpuinfo.domain.model.CpuData
import com.kgurgul.cpuinfo.itemValue
import com.kgurgul.cpuinfo.verticalDivider

class CpuInfoEpoxyController(
        private val context: Context
) : TypedEpoxyController<CpuInfoViewState>() {

    override fun buildModels(data: CpuInfoViewState) {
        buildFrequencies(data.cpuData.frequencies)
        if (data.cpuData.frequencies.isNotEmpty()) {
            verticalDivider { id("frequency_divider") }
        }
        itemValue {
            id("soc_name")
            title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_soc_name))
            value(data.cpuData.processorName)
        }
        verticalDivider { id("soc_name_divider") }
        itemValue {
            id("abi")
            title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_abi))
            value(data.cpuData.abi)
        }
        verticalDivider { id("abi_divider") }
        itemValue {
            id("cores")
            title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_cores))
            value(data.cpuData.coreNumber.toString())
        }
        verticalDivider { id("cores_divider") }
        itemValue {
            id("has_neon")
            title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_has_neon))
            value(
                    if (data.cpuData.hasArmNeon) {
                        this@CpuInfoEpoxyController.context.getString(R.string.yes)
                    } else {
                        this@CpuInfoEpoxyController.context.getString(R.string.no)
                    }
            )
        }
        buildCaches(data.cpuData)
    }

    private fun buildFrequencies(frequencies: List<CpuData.Frequency>) {
        frequencies.forEachIndexed { i, frequency ->
            val currentFreq = if (frequency.current != -1L) {
                context.getString(R.string.cpu_current_frequency, i, frequency.current.toString())
            } else {
                context.getString(R.string.cpu_frequency_stopped, i)
            }
            val minFreq = if (frequency.min != -1L) {
                context.getString(R.string.cpu_frequency, "0")
            } else {
                ""
            }
            val maxFreq = if (frequency.max != -1L) {
                context.getString(R.string.cpu_frequency, frequency.max.toString())
            } else {
                ""
            }
            cpuFrequency {
                id("frequency$i")
                maxFrequency(frequency.max)
                currentFrequency(frequency.current)
                currentFrequencyDescription(currentFreq)
                minFrequencyDescription(minFreq)
                maxFrequencyDescription(maxFreq)
            }
        }
    }

    private fun buildCaches(cpuData: CpuData) {
        if (cpuData.l1dCaches.isNotEmpty()) {
            verticalDivider { id("l1d_divider") }
            itemValue {
                id("l1d")
                title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_l1d))
                value(cpuData.l1dCaches)
            }

        }
        if (cpuData.l1iCaches.isNotEmpty()) {
            verticalDivider { id("l1i_divider") }
            itemValue {
                id("l1i")
                title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_l1i))
                value(cpuData.l1iCaches)
            }
        }
        if (cpuData.l2Caches.isNotEmpty()) {
            verticalDivider { id("l2_divider") }
            itemValue {
                id("l2")
                title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_l2))
                value(cpuData.l2Caches)
            }
        }
        if (cpuData.l3Caches.isNotEmpty()) {
            verticalDivider { id("l3_divider") }
            itemValue {
                id("l3")
                title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_l3))
                value(cpuData.l3Caches)
            }
        }
        if (cpuData.l4Caches.isNotEmpty()) {
            verticalDivider { id("l4_divider") }
            itemValue {
                id("l4")
                title(this@CpuInfoEpoxyController.context.getString(R.string.cpu_l4))
                value(cpuData.l4Caches)
            }
        }
    }
}