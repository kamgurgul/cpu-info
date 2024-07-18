package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.sensors_accelerometer
import com.kgurgul.cpuinfo.utils.round4
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.koin.core.annotation.Factory
import platform.CoreMotion.CMAcceleration
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

@Factory
actual class SensorsInfoProvider actual constructor() {

    private val queue = NSOperationQueue.mainQueue
    private val manager = CMMotionManager().apply {
        deviceMotionUpdateInterval = 100.0 / 1000.0
    }

    actual fun getSensorData(): Flow<List<SensorData>> = callbackFlow {
        startProvidingAccelerometerData(channel)

        awaitClose {
            manager.stopDeviceMotionUpdates()
        }
    }

    private fun startProvidingAccelerometerData(channel: SendChannel<List<SensorData>>) {
        manager.startDeviceMotionUpdatesUsingReferenceFrame(
            referenceFrame = CMAttitudeReferenceFrameXMagneticNorthZVertical,
            toQueue = queue
        ) { motion, error ->
            if (error != null) {
                return@startDeviceMotionUpdatesUsingReferenceFrame
            }
            motion?.let { channel.trySend(listOf(it.toSensorData())) }
        }
    }

    private fun CMDeviceMotion.toSensorData(): SensorData {
        val value = buildString {
            appendLine("\tAcceleration:")
            appendLine(convertAcceleration(userAcceleration))
            appendLine("\tGravity:")
            appendLine(convertAcceleration(gravity))
            append("\tHeading: ${heading.round4()}")
        }
        return SensorData(
            id = ID_ACCELEROMETER,
            name = runBlocking { getString(Res.string.sensors_accelerometer) },
            value = value,
        )
    }

    private fun convertAcceleration(acceleration: CValue<CMAcceleration>): String {
        return acceleration.useContents {
            "\t\tX=${x.round4()}G\n\t\tY=${y.round4()}G\n\t\tZ=${z.round4()}G"
        }
    }

    companion object {
        private const val ID_ACCELEROMETER = "ID_ACCELEROMETER"
    }
}