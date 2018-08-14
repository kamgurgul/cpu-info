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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter
import kotlinx.android.synthetic.main.item_temperature.view.*

/**
 * Temperature list adapter which observe temperatureListLiveData
 *
 * @author kgurgul
 */
class TemperatureAdapter(private val temperatureFormatter: TemperatureFormatter,
                         private val temperatureList: List<TemperatureItem>)
    : RecyclerView.Adapter<TemperatureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_temperature, parent,
                false)
        return ViewHolder(view, temperatureFormatter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(temperatureList[position])
    }

    override fun getItemCount(): Int = temperatureList.size

    class ViewHolder(
            private val view: View,
            private val temperatureFormatter: TemperatureFormatter)
        : RecyclerView.ViewHolder(view) {

        fun bind(temperatureItem: TemperatureItem) {
            view.temperatureIv.setImageResource(temperatureItem.iconRes)
            view.temperatureTypeTv.text = temperatureItem.name
            view.temperatureTv.text = temperatureFormatter.format(temperatureItem.temperature)
        }
    }
}

