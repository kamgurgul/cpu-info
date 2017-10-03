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

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.databinding.ItemTemperatureBinding
import com.kgurgul.cpuinfo.features.temperature.TemperatureFormatter

/**
 * Temperature list adapter which observe temperatureItemsLiveData list
 *
 * @author kgurgul
 */
class TemperatureAdapter(private val temperatureFormatter: TemperatureFormatter)
    : RecyclerView.Adapter<TemperatureAdapter.BindingViewHolder>() {

    var temperatureItems: List<TemperatureItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val viewHolderBinding = DataBindingUtil.inflate<ItemTemperatureBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_temperature,
                parent,
                false)
        return BindingViewHolder(viewHolderBinding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val temperatureItem = temperatureItems!![position]
        holder.bindTemperatureItem(temperatureItem, temperatureFormatter)
    }

    override fun getItemCount(): Int {
        return temperatureItems?.size ?: 0
    }

    fun setTempItems(tempItems: List<TemperatureItem>?) {
        temperatureItems = tempItems
        notifyDataSetChanged()
    }

    class BindingViewHolder(private val binding: ItemTemperatureBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bindTemperatureItem(temperatureItem: TemperatureItem,
                                temperatureFormatter: TemperatureFormatter) {
            if (binding.viewModel == null) {
                binding.viewModel = TemperatureItemViewModel(temperatureItem, temperatureFormatter)
            } else {
                binding.viewModel.setTempItem(temperatureItem)
            }
        }
    }
}

