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
package com.kgurgul.cpuinfo.auto.ui

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kgurgul.cpuinfo.auto.features.CarInfoCategory
import com.kgurgul.cpuinfo.auto.features.CarInfoViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Displays live values of a single [CarInfoCategory]. */
class CarInfoDetailScreen(
    carContext: CarContext,
    viewModel: CarInfoViewModel,
    category: CarInfoCategory,
    private val title: String,
) : Screen(carContext) {

    private var items: List<CarInfoItem>? = null

    init {
        collectAndInvalidate(
            viewModel.uiStateFlow
                .map { uiState ->
                    if (uiState.isInitializing) null
                    else CarInfoItemsMapper.map(uiState.carData, category)
                }
                .distinctUntilChanged()
        ) {
            items = it
        }
    }

    override fun onGetTemplate(): Template {
        val items = items
        val builder = ListTemplate.Builder().setHeaderCompat(carContext, title, Action.BACK)
        if (items == null) {
            return builder.setLoading(true).build()
        }
        val itemList = ItemList.Builder()
        items.take(carContext.listContentLimit()).forEach { item ->
            itemList.addItem(Row.Builder().setTitle(item.title).addText(item.value).build())
        }
        return builder.setSingleList(itemList.build()).build()
    }
}
