package com.kgurgul.cpuinfo.features.cputile

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.data.provider.CpuDataProvider
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RequiresApi(api = Build.VERSION_CODES.N)
class CpuTileService :
    TileService(),
    CoroutineScope,
    KoinComponent {

    private val cpuDataProvider: CpuDataProvider by inject()
    private val dispatchersProvider: IDispatchersProvider by inject()

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + dispatchersProvider.main

    private var refreshingJob: Job? = null

    private val minMaxAvg: Pair<Long, Long> by lazy {
        val cpuCount = cpuDataProvider.getNumberOfCores()
        val minFreq = mutableListOf<Long>()
        val maxFreq = mutableListOf<Long>()
        for (i in 0 until cpuCount) {
            val minMax = cpuDataProvider.getMinMaxFreq(i)
            minFreq.add(minMax.first)
            maxFreq.add(minMax.second)
        }
        Pair(minFreq.sum() / cpuCount, maxFreq.sum() / cpuCount)
    }

    private val icons by lazy {
        mapOf(
            CPULoad.Low to Icon.createWithResource(this, R.drawable.ic_cpu_low),
            CPULoad.Medium to Icon.createWithResource(this, R.drawable.ic_cpu_med),
            CPULoad.High to Icon.createWithResource(this, R.drawable.ic_cpu_high),
        )
    }
    private val defaultIcon by lazy { Icon.createWithResource(this, R.drawable.ic_cpu_high) }

    override fun onStartListening() {
        super.onStartListening()
        refreshingJob?.cancel()
        refreshingJob = launch {
            while (true) {
                val load = getAverageCPUFreq()
                qsTile.label = "Avg ${load}MHz"
                qsTile.icon = getLoadIcon(load)
                qsTile.updateTile()
                delay(REFRESHING_DELAY_MS)
            }
        }
    }

    override fun onStopListening() {
        refreshingJob?.cancel()
        super.onStopListening()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }

    private fun getLoadIcon(avgLoad: Long): Icon {
        val freqDiff = minMaxAvg.second - minMaxAvg.first
        val freqThirds = freqDiff / 3
        val loadEnum = when {
            avgLoad >= minMaxAvg.second - freqThirds -> CPULoad.High
            minMaxAvg.second - freqThirds > avgLoad
                && avgLoad >= minMaxAvg.first + freqThirds -> CPULoad.Medium

            else -> CPULoad.Low
        }
        return icons.getOrDefault(loadEnum, defaultIcon)
    }

    private suspend fun getAverageCPUFreq(): Long {
        return withContext(dispatchersProvider.io) {
            val cpuCount = cpuDataProvider.getNumberOfCores()
            var sumFreq = 0L
            for (i in 0 until cpuCount) {
                sumFreq += cpuDataProvider.getCurrentFreq(i)
            }
            sumFreq / cpuCount
        }
    }

    enum class CPULoad {
        Low,
        Medium,
        High,
    }

    companion object {
        private const val REFRESHING_DELAY_MS = 1000L
    }
}
