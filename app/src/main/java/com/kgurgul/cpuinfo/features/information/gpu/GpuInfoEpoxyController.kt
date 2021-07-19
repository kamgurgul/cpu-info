package com.kgurgul.cpuinfo.features.information.gpu

import android.content.Context
import com.airbnb.epoxy.TypedEpoxyController
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.itemValue
import com.kgurgul.cpuinfo.verticalDivider

class GpuInfoEpoxyController(
        private val context: Context
) : TypedEpoxyController<GpuInfoViewState>() {

    override fun buildModels(data: GpuInfoViewState) {
        itemValue {
            id("gles_version")
            title(this@GpuInfoEpoxyController.context.getString(R.string.gles_version))
            value(data.gpuData.glesVersio)
        }
        if (data.gpuData.glVendor != null) {
            verticalDivider { id("gl_vendor_divider") }
            itemValue {
                id("gl_vendor")
                title(this@GpuInfoEpoxyController.context.getString(R.string.vendor))
                value(data.gpuData.glVendor)
            }
        }
        if (data.gpuData.glRenderer != null) {
            verticalDivider { id("gl_renderer_divider") }
            itemValue {
                id("gl_renderer")
                title(this@GpuInfoEpoxyController.context.getString(R.string.renderer))
                value(data.gpuData.glRenderer)
            }
        }
        if (data.gpuData.glExtensions != null) {
            verticalDivider { id("gl_extensions_divider") }
            itemValue {
                id("gl_extensions")
                title(this@GpuInfoEpoxyController.context.getString(R.string.extensions))
                value(data.gpuData.glExtensions)
            }
        }
    }
}