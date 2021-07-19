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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kgurgul.cpuinfo.databinding.ItemTemperatureBinding
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter

/**
 * Temperature list adapter which observe temperatureListLiveData
 *
 * @author kgurgul
 */
class TemperatureAdapter(
        private val temperatureFormatter: TemperatureFormatter,
        private val temperatureList: List<TemperatureItem>
) : RecyclerView.Adapter<TemperatureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemperatureBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, temperatureFormatter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(temperatureList[position])
    }

    override fun getItemCount(): Int = temperatureList.size

    class ViewHolder(
            private val binding: ItemTemperatureBinding,
            private val temperatureFormatter: TemperatureFormatter
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(temperatureItem: TemperatureItem) {
            with(binding) {
                temperatureIv.setImageResource(temperatureItem.iconRes)
                temperatureTypeTv.text = temperatureItem.name
                temperatureTv.text = temperatureFormatter.format(temperatureItem.temperature)
            }
        }
    }
}

