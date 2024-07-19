package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.utils.round4
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Factory
import platform.CoreMotion.CMAcceleration
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMotionManager
import platform.CoreMotion.CMRotationRate
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
            motion?.let { channel.trySend(it.toSensorData()) }
        }
    }

    private fun CMDeviceMotion.toSensorData(): List<SensorData> {
        return listOf(
            SensorData(
                id = ID_ACCELERATION,
                name = "Acceleration",
                value = convertAcceleration(userAcceleration),
            ),
            SensorData(
                id = ID_GRAVITY,
                name = "Gravity",
                value = convertAcceleration(gravity),
            ),
            SensorData(
                id = ID_HEADING,
                name = "Heading",
                value = convertAcceleration(gravity),
            ),
            SensorData(
                id = ID_ROTATION_RATE,
                name = "Rotation rate",
                value = convertRotationRate(rotationRate),
            ),
        )
    }

    private fun convertAcceleration(acceleration: CValue<CMAcceleration>): String {
        return acceleration.useContents {
            "X=${x.round4()}G  Y=${y.round4()}G  Z=${z.round4()}G"
        }
    }

    private fun convertRotationRate(acceleration: CValue<CMRotationRate>): String {
        return acceleration.useContents {
            "X=${x.round4()}  Y=${y.round4()}  Z=${z.round4()}"
        }
    }

    companion object {
        private const val ID_ACCELERATION = "ID_ACCELERATION"
        private const val ID_GRAVITY = "ID_GRAVITY"
        private const val ID_HEADING = "ID_HEADING"
        private const val ID_ROTATION_RATE = "ID_ROTATION_RATE"
    }
}