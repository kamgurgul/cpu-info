/*
 * Copyright KG Soft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.SensorData
import com.kgurgul.cpuinfo.domain.model.TextResource
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.sensors_acceleration
import com.kgurgul.cpuinfo.shared.sensors_attitude
import com.kgurgul.cpuinfo.shared.sensors_gravity
import com.kgurgul.cpuinfo.shared.sensors_magnetic_field
import com.kgurgul.cpuinfo.shared.sensors_rotation_rate
import com.kgurgul.cpuinfo.utils.round4
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreMotion.CMAcceleration
import platform.CoreMotion.CMAttitude
import platform.CoreMotion.CMAttitudeReferenceFrameXMagneticNorthZVertical
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMagneticField
import platform.CoreMotion.CMMagneticFieldCalibrationAccuracyUncalibrated
import platform.CoreMotion.CMMotionManager
import platform.CoreMotion.CMRotationRate
import platform.Foundation.NSOperationQueue

actual class SensorsInfoProvider actual constructor() {

    private val queue = NSOperationQueue.mainQueue
    private val manager = CMMotionManager().apply { deviceMotionUpdateInterval = 100.0 / 1000.0 }

    actual fun getSensorData(): Flow<List<SensorData>> = callbackFlow {
        startProvidingAccelerometerData(channel)

        awaitClose { manager.stopDeviceMotionUpdates() }
    }

    private fun startProvidingAccelerometerData(channel: SendChannel<List<SensorData>>) {
        manager.startDeviceMotionUpdatesUsingReferenceFrame(
            referenceFrame = CMAttitudeReferenceFrameXMagneticNorthZVertical,
            toQueue = queue,
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
                    name = TextResource.Resource(Res.string.sensors_acceleration),
                    value = convertAcceleration(userAcceleration),
                )
            )
            add(
                SensorData(
                    id = ID_GRAVITY,
                    name = TextResource.Resource(Res.string.sensors_gravity),
                    value = convertAcceleration(gravity),
                )
            )
            add(
                SensorData(
                    id = ID_ROTATION_RATE,
                    name = TextResource.Resource(Res.string.sensors_rotation_rate),
                    value = convertRotationRate(rotationRate),
                )
            )
            magneticField.useContents {
                if (accuracy != CMMagneticFieldCalibrationAccuracyUncalibrated) {
                    add(
                        SensorData(
                            id = ID_MAGNETIC_FIELD,
                            name = TextResource.Resource(Res.string.sensors_magnetic_field),
                            value = convertMagneticField(field),
                        )
                    )
                }
            }
            add(
                SensorData(
                    id = ID_ATTITUDE,
                    name = TextResource.Resource(Res.string.sensors_attitude),
                    value = convertAttitude(attitude),
                )
            )
        }
    }

    private fun convertAcceleration(acceleration: CValue<CMAcceleration>): String {
        return acceleration.useContents { "X=${x.round4()}G  Y=${y.round4()}G  Z=${z.round4()}G" }
    }

    private fun convertRotationRate(rotationRate: CValue<CMRotationRate>): String {
        return rotationRate.useContents { "X=${x.round4()}  Y=${y.round4()}  Z=${z.round4()}" }
    }

    private fun convertMagneticField(magneticField: CMMagneticField): String {
        return with(magneticField) { "X=${x.round4()}  Y=${y.round4()}  Z=${z.round4()}" }
    }

    private fun convertAttitude(attitude: CMAttitude): String {
        return with(attitude) {
            "Pitch=${pitch.round4()}  Roll=${roll.round4()}  Yaw=${yaw.round4()}"
        }
    }

    companion object {
        private const val ID_ACCELERATION = "ID_ACCELERATION"
        private const val ID_GRAVITY = "ID_GRAVITY"
        private const val ID_ROTATION_RATE = "ID_ROTATION_RATE"
        private const val ID_MAGNETIC_FIELD = "ID_ROTATION_RATE"
        private const val ID_ATTITUDE = "ID_ATTITUDE"
    }
}
