package com.kgurgul.cpuinfo.features.information.cpu

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.cpuFrequency

class CpuInfoEpoxyController(
        private val context: Context
) : TypedEpoxyController<CpuInfoViewState>() {

    override fun buildModels(data: CpuInfoViewState) {
        data.cpuData.frequencies.forEachIndexed { i, frequency ->
            val currentFreq = context.getString(
                    R.string.cpu_current_frequency,
                    i,
                    frequency.current
            )
            val minMaxFreq = context.getString(
                    R.string.cpu_min_max_frequency,
                    frequency.min,
                    frequency.max
            )
            cpuFrequency {
                id("frequency$i")
                minFrequency(frequency.min)
                maxFrequency(frequency.max)
                currentFrequency(frequency.current)
                currentFrequencyDescription(currentFreq)
                minMaxFrequencyDescription(minMaxFreq)
            }
        }
    }
}