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