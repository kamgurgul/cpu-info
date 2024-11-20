package com.kgurgul.cpuinfo.data.provider

import com.kgurgul.cpuinfo.domain.model.ItemValue
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.renderer
import com.kgurgul.cpuinfo.shared.vendor
import com.kgurgul.cpuinfo.shared.version
import kotlinx.browser.document
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement

actual class GpuDataProvider actual constructor() : IGpuDataProvider {

    actual override suspend fun getData(): List<ItemValue> {
        return buildList {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl") as? WebGLRenderingContext
            gl?.getParameter(WebGLRenderingContext.VENDOR)?.let {
                add(ItemValue.NameResource(Res.string.vendor, it.toString()))
            }
            gl?.getParameter(WebGLRenderingContext.RENDERER)?.let {
                add(ItemValue.NameResource(Res.string.renderer, it.toString()))
            }
            gl?.getParameter(WebGLRenderingContext.VERSION)?.let {
                add(ItemValue.NameResource(Res.string.version, it.toString()))
            }
        }
    }
}

