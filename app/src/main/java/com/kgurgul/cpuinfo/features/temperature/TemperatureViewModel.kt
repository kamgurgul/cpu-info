/*
 * Copyright 2017 KG Soft
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

package com.kgurgul.cpuinfo.features.temperature

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import android.databinding.ObservableBoolean
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.common.Prefs
import com.kgurgul.cpuinfo.features.temperature.list.TemperatureItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel for [TemperatureActivity]
 *
 * @author kgurgul
 */
class TemperatureViewModel @Inject constructor(
        private val prefs: Prefs,
        private val resources: Resources,
        private val temperatureProvider: TemperatureProvider)
    : ViewModel() {
    private val CPU_TEMP_RESULT_KEY = "temp_result_key"

    // Binding fields
    val isLoading = ObservableBoolean(false)
    val isError = ObservableBoolean(false)

    val temperatureItemsLiveData = MutableLiveData<List<TemperatureItem>>()

    private var temperatureDisposable: Disposable? = null
    private var refreshingDisposable: Disposable? = null
    private var cpuTemperatureResult: TemperatureProvider.CpuTemperatureResult? = null
    private var isBatteryTemperatureAvailable = false

    /**
     * Start temperature getting process. It also validates all temperatures availability.
     */
    fun startTemperatureRefreshing() {
        Timber.i("startTemperatureRefreshing()")
        if (prefs.contains(CPU_TEMP_RESULT_KEY)) {
            cpuTemperatureResult = prefs.get(CPU_TEMP_RESULT_KEY,
                    TemperatureProvider.CpuTemperatureResult())
            verifyTemperaturesAvailability()
        } else {
            temperatureDisposable = getCpuAvailabilityTest()
        }
    }

    /**
     * Stop temperature getting process
     */
    fun stopTemperatureRefreshing() {
        Timber.i("stopTemperatureRefreshing()")
        refreshingDisposable?.dispose()
    }

    /**
     * Try to find path with CPU temperature. If success try validate temperatures and schedule
     * refreshing process.
     */
    private fun getCpuAvailabilityTest(): Disposable {
        return temperatureProvider.getCpuTemperatureFinder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoading.set(true)
                    isError.set(false)
                }
                .doFinally {
                    isLoading.set(false)
                    verifyTemperaturesAvailability()
                }
                .subscribeBy(
                        onSuccess = { temperatureResult ->
                            prefs.insert(CPU_TEMP_RESULT_KEY, temperatureResult)
                            cpuTemperatureResult = temperatureResult
                        },
                        onError = { throwable ->
                            Timber.e(throwable)
                        },
                        onComplete = {
                            Timber.i("List scan complete")
                        }
                )
    }

    /**
     * Verify which temperatures are available and schedule refreshing. If we don't have any
     * temperature info set isError flag to true
     */
    private fun verifyTemperaturesAvailability() {
        if (!isBatteryTemperatureAvailable) {
            val batteryTemp = temperatureProvider.getBatteryTemperature()
            if (batteryTemp != 0) {
                isBatteryTemperatureAvailable = true
            }
        }

        if (isBatteryTemperatureAvailable || cpuTemperatureResult != null) {
            scheduleRefreshing()
        } else {
            isError.set(true)
        }
    }

    /**
     * Schedule refreshing process (for 3s)
     */
    private fun scheduleRefreshing() {
        if (refreshingDisposable != null) {
            refreshingDisposable!!.dispose()
        }

        refreshingDisposable = Observable.interval(0, 3, TimeUnit.SECONDS)
                .map {
                    var batteryTemp: Int? = null
                    if (isBatteryTemperatureAvailable) {
                        batteryTemp = temperatureProvider.getBatteryTemperature()
                    }
                    var cpuTemp: Float? = null
                    if (cpuTemperatureResult != null) {
                        cpuTemp = temperatureProvider.getCpuTemp(cpuTemperatureResult!!.filePath)
                    }
                    TempContainer(cpuTemp, batteryTemp)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        { (cpuTemp, batteryTemp) ->
                            val temporaryTempList = ArrayList<TemperatureItem>()
                            if (cpuTemp != null) {
                                temporaryTempList.add(TemperatureItem(R.drawable.ic_cpu_temp,
                                        resources.getString(R.string.cpu), cpuTemp))
                            }
                            if (batteryTemp != null) {
                                temporaryTempList.add(TemperatureItem(R.drawable.ic_battery,
                                        resources.getString(R.string.battery),
                                        batteryTemp.toFloat()))
                            }

                            temperatureItemsLiveData.value = temporaryTempList
                        },
                        { throwable ->
                            Timber.e(throwable)
                        }
                )
    }

    override fun onCleared() {
        super.onCleared()
        Timber.i("onCleared()")
        temperatureDisposable?.dispose()
        refreshingDisposable?.dispose()
    }

    private data class TempContainer(val cpuTemp: Float?, val batteryTemp: Int?)
}