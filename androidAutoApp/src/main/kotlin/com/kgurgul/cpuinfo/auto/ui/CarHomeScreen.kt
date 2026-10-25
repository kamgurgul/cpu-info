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

import android.content.pm.PackageManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kgurgul.cpuinfo.auto.R
import com.kgurgul.cpuinfo.auto.features.CarInfoCategory
import com.kgurgul.cpuinfo.auto.features.CarInfoViewModel
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.app_name
import com.kgurgul.cpuinfo.shared.car_check_phone
import com.kgurgul.cpuinfo.shared.car_grant_permissions
import com.kgurgul.cpuinfo.shared.car_grant_permissions_description
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

/** Root car screen with list of available car information categories. */
class CarHomeScreen(carContext: CarContext, private val viewModel: CarInfoViewModel) :
    Screen(carContext) {

    private var content: Content? = null

    init {
        collectAndInvalidate(
            viewModel.uiStateFlow
                .map { it.carData.missingPermissions.isNotEmpty() }
                .distinctUntilChanged()
                .map { hasMissingPermissions -> createContent(hasMissingPermissions) }
        ) {
            content = it
        }
        observeAsEvents(viewModel.events) { event ->
            when (event) {
                is CarInfoViewModel.Event.RequestPermissions -> {
                    // On Android Auto the permission dialog is displayed on the phone
                    if (!isAutomotive()) {
                        CarToast.makeText(
                                carContext,
                                getString(Res.string.car_check_phone),
                                CarToast.LENGTH_LONG,
                            )
                            .show()
                    }
                    carContext.requestPermissions(event.permissions) { _, _ ->
                        viewModel.onPermissionsResult()
                    }
                }
            }
        }
    }

    override fun onGetTemplate(): Template {
        val content = content
        val builder =
            ListTemplate.Builder().setHeaderCompat(carContext, content?.title, Action.APP_ICON)
        if (content == null) {
            return builder.setLoading(true).build()
        }
        val itemList = ItemList.Builder()
        content.permissionsRow?.let { (title, description) ->
            itemList.addItem(
                Row.Builder()
                    .setTitle(title)
                    .addText(description)
                    .setImage(carContext.carIcon(R.drawable.ic_car_permission))
                    .setOnClickListener { viewModel.onGrantPermissionsClick() }
                    .build()
            )
        }
        content.categories.forEach { (category, title) ->
            itemList.addItem(
                Row.Builder()
                    .setTitle(title)
                    .setImage(carContext.carIcon(category.icon))
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(
                            CarInfoDetailScreen(
                                carContext = carContext,
                                viewModel = viewModel,
                                category = category,
                                title = title,
                            )
                        )
                    }
                    .build()
            )
        }
        return builder.setSingleList(itemList.build()).build()
    }

    private fun isAutomotive() =
        carContext.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)

    private suspend fun createContent(hasMissingPermissions: Boolean) =
        Content(
            title = getString(Res.string.app_name),
            permissionsRow =
                if (hasMissingPermissions) {
                    getString(Res.string.car_grant_permissions) to
                        getString(Res.string.car_grant_permissions_description)
                } else {
                    null
                },
            categories =
                CarInfoCategory.entries
                    .map { it to getString(it.title) }
                    .take(carContext.listContentLimit() - if (hasMissingPermissions) 1 else 0),
        )

    private data class Content(
        val title: String,
        val permissionsRow: Pair<String, String>?,
        val categories: List<Pair<CarInfoCategory, String>>,
    )
}
