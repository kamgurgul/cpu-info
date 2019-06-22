package com.kgurgul.cpuinfo.features.cputile

import android.os.Build
import android.os.Handler
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import android.graphics.drawable.Icon
import com.kgurgul.cpuinfo.R
import java.io.RandomAccessFile

@RequiresApi(api = Build.VERSION_CODES.N)
class CPUTile : TileService() {

    enum class CPULoad {
        Low,
        Medium,
        High
    }

    private val handler = Handler()
    private var runnable = Runnable {}

    private val minMaxAvg : Pair<Long, Long> by lazy {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val minFreq = mutableListOf<Long>()
        val maxFreq = mutableListOf<Long>()
        for (i in 0 until cpuCount) {
            val minMax = tryToGetMinMaxFreq(i)
            minFreq.add(minMax.first)
            maxFreq.add(minMax.second)
        }
        Pair(minFreq.sum() / cpuCount, maxFreq.sum() / cpuCount)
    }

    private val icons by lazy { mapOf<CPULoad, Icon>(
        CPULoad.Low to Icon.createWithResource(this, R.drawable.ic_cpu_low),
        CPULoad.Medium to Icon.createWithResource(this, R.drawable.ic_cpu_med),
        CPULoad.High to Icon.createWithResource(this, R.drawable.ic_cpu_high)
    )}
    private val defaultIcon by lazy { Icon.createWithResource(this, R.drawable.ic_cpu_high) }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        runnable = Runnable {
            val load = averageCPUFreq()
            qsTile.label = "Avg ${load}MHz"
            qsTile.icon = getLoadIcon(load)
            qsTile.updateTile()
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed (runnable, 1)
    }

    override fun onStopListening() {
        super.onStopListening()
        qsTile.updateTile()
        handler.removeCallbacks(runnable)
    }

    private fun getLoadIcon(avgLoad: Long) : Icon {
        val freqDiff = minMaxAvg.second - minMaxAvg.first
        val freqThirds = freqDiff / 3
        val loadEnum = when {
            avgLoad >= minMaxAvg.second - freqThirds -> CPULoad.High
            minMaxAvg.second - freqThirds > avgLoad && avgLoad >= minMaxAvg.first + freqThirds -> CPULoad.Medium
            else -> CPULoad.Low
        }
        return icons.getOrDefault(loadEnum, defaultIcon)
    }

    private fun averageCPUFreq() : Long {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        var sumFreq = 0L
        for (i in 0 until cpuCount) {
            sumFreq += tryToGetCurrentFreq(i)
        }
        return (sumFreq / cpuCount)
    }

    private fun tryToGetCurrentFreq(coreNumber: Int): Long {
        val filePath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/scaling_cur_freq"
        try {
            val reader = RandomAccessFile(filePath, "r")
            val value = reader.readLine().toLong() / 1000
            reader.close()
            return value
        } catch (ignored: Exception) {
        }
        return 0
    }

    private fun tryToGetMinMaxFreq(coreNumber: Int): Pair<Long, Long> {
        val minPath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/cpuinfo_min_freq"
        val maxPath = "/sys/devices/system/cpu/cpu$coreNumber/cpufreq/cpuinfo_max_freq"
        try {
            var reader = RandomAccessFile(minPath, "r")
            val minMhz = reader.readLine().toLong() / 1000
            reader.close()

            reader = RandomAccessFile(maxPath, "r")
            val maxMhz = reader.readLine().toLong() / 1000
            reader.close()
            return Pair(minMhz, maxMhz)
        } catch (e: Exception) {
        }
        return Pair(0, 0)
    }
}