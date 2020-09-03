package com.kgurgul.cpuinfo.features.information.base

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.kgurgul.cpuinfo.features.information.base.model.InfoItemViewState

class InfoItemsEpoxyController(
        private val context: Context
) : TypedEpoxyController<InfoItemViewState>() {

    override fun buildModels(data: InfoItemViewState) {

    }
}