package com.kgurgul.cpuinfo.features.cputile

import android.os.Build
import android.os.Handler
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import java.io.RandomAccessFile

@RequiresApi(api = Build.VERSION_CODES.N)
class CPUTile : TileService() {

    private val handler = Handler()
    private var runnable = Runnable {}

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        runnable = Runnable {
            qsTile.label = "${averageCPUFreq()} MHz"
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
}