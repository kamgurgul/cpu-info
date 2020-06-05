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
            title(context.getString(R.string.cpu_soc_name))
            value(data.cpuData.processorName)
        }
        verticalDivider { id("soc_name_divider") }
        itemValue {
            id("cores")
            title(context.getString(R.string.cpu_cores))
            value(data.cpuData.coreNumber.toString())
        }
        verticalDivider { id("cores_divider") }
        itemValue {
            id("has_neon")
            title(context.getString(R.string.cpu_has_neon))
            value(
                    if (data.cpuData.hasArmNeon) {
                        context.getString(R.string.yes)
                    } else {
                        context.getString(R.string.no)
                    }
            )
        }
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
}