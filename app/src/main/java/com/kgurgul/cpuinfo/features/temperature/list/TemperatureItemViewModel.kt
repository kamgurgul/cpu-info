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

package com.kgurgul.cpuinfo.features.temperature.list

import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter

/**
 * ViewModel for temperature item
 *
 * @author kgurgul
 */
class TemperatureItemViewModel(
        private var temperatureItem: TemperatureItem,
        private val temperatureFormatter: TemperatureFormatter) : BaseObservable() {

    companion object {
        @JvmStatic
        @BindingAdapter("srcCompat")
        fun loadImage(imageView: ImageView, image: Int) {
            imageView.setImageResource(image)
        }
    }

    fun setTempItem(temperatureItem: TemperatureItem) {
        this.temperatureItem = temperatureItem
        notifyChange()
    }

    fun getUnitIcon(): Int =
            temperatureItem.iconRes

    fun getUnitName(): String =
            temperatureItem.name

    fun getTempValue(): String =
            temperatureFormatter.format(temperatureItem.temperature)
}