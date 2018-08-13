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

package com.kgurgul.cpuinfo.features.information.base

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kgurgul.cpuinfo.R

/**
 * Adapter for all info items inside [BaseRvFragment]. It should support two types of layouts:
 * vertical and horizontal. It also change color of the first string from passed [Pair] to red if
 * the second one is empty.
 *
 * @author kgurgul
 */
class InfoItemsAdapter(
        private val context: Context,
        private val itemsObservableList: List<Pair<String, String>>,
        private val layoutType: LayoutType = InfoItemsAdapter.LayoutType.HORIZONTAL_LAYOUT)
    : RecyclerView.Adapter<InfoItemsAdapter.SingleItemViewHolder>() {

    enum class LayoutType {
        HORIZONTAL_LAYOUT, VERTICAL_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleItemViewHolder {
        val layout = if (layoutType == LayoutType.HORIZONTAL_LAYOUT)
            R.layout.item_value else R.layout.item_value_vertical
        return SingleItemViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent,
                false))
    }

    override fun onBindViewHolder(holder: SingleItemViewHolder, position: Int) {
        val item = itemsObservableList[position]
        holder.titleTv.text = item.first
        holder.valueTv.text = item.second

        if (item.second.isEmpty()) {
            holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            holder.valueTv.visibility = View.GONE
        } else {
            holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary))
            holder.valueTv.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = itemsObservableList.size

    class SingleItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTv: TextView = itemView.findViewById(R.id.title_tv)
        val valueTv: TextView = itemView.findViewById(R.id.value_tv)
    }
}