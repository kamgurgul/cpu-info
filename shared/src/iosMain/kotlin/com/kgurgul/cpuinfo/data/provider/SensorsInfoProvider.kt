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
import platform.CoreMotion.CMAttitude
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMagneticField
import platform.CoreMotion.CMMagneticFieldCalibrationAccuracyUncalibrated
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
        return buildList {
            add(
                SensorData(
                    id = ID_ACCELERATION,
                    name = "Acceleration",
                    value = convertAcceleration(userAcceleration),
                )
            )
            add(
                SensorData(
                    id = ID_GRAVITY,
                    name = "Gravity",
                    value = convertAcceleration(gravity),
                )
            )
            add(
                SensorData(
                    id = ID_HEADING,
                    name = "Heading",
                    value = convertAcceleration(gravity),
                )
            )
            add(
                SensorData(
                    id = ID_ROTATION_RATE,
                    name = "Rotation rate",
                    value = convertRotationRate(rotationRate),
                )
            )
            magneticField.useContents {
                if (accuracy != CMMagneticFieldCalibrationAccuracyUncalibrated) {
                    add(
                        SensorData(
                            id = ID_MAGNETIC_FIELD,
                            name = "Magnetic field",
                            value = convertMagneticField(field),
                        )
                    )
                }
            }
            add(
                SensorData(
                    id = ID_ATTITUDE,
                    name = "Attitude",
                    value = convertAttitude(attitude),
                )
            )
        }
    }

    private fun convertAcceleration(acceleration: CValue<CMAcceleration>): String {
        return acceleration.useContents {
            "X=${x.round4()}G  Y=${y.round4()}G  Z=${z.round4()}G"
        }
    }

    private fun convertRotationRate(rotationRate: CValue<CMRotationRate>): String {
        return rotationRate.useContents {
            "X=${x.round4()}  Y=${y.round4()}  Z=${z.round4()}"
        }
    }

    private fun convertMagneticField(magneticField: CMMagneticField): String {
        return with(magneticField) {
            "X=${x.round4()}  Y=${y.round4()}  Z=${z.round4()}"
        }
    }

    private fun convertAttitude(attitude: CMAttitude): String {
        return with(attitude) {
            "Pitch=${pitch.round4()}  Roll=${roll.round4()}  Yaw=${yaw.round4()}"
        }
    }

    companion object {
        private const val ID_ACCELERATION = "ID_ACCELERATION"
        private const val ID_GRAVITY = "ID_GRAVITY"
        private const val ID_HEADING = "ID_HEADING"
        private const val ID_ROTATION_RATE = "ID_ROTATION_RATE"
        private const val ID_MAGNETIC_FIELD = "ID_ROTATION_RATE"
        private const val ID_ATTITUDE = "ID_ATTITUDE"
    }
}