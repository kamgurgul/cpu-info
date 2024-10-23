package com.kgurgul.cpuinfo.data.provider

import kotlinx.browser.document
import org.khronos.webgl.WebGLRenderingContext
import org.koin.core.component.KoinComponent
import org.w3c.dom.HTMLCanvasElement

actual class GpuDataProvider actual constructor() : KoinComponent {

    actual suspend fun getData(): List<Pair<String, String>> {
        return buildList {
            val canvas = document.createElement("canvas") as HTMLCanvasElement
            val gl = canvas.getContext("webgl") as? WebGLRenderingContext
            gl?.getParameter(WebGLRenderingContext.VENDOR)?.let {
                add("Vendor" to it.toString())
            }
            gl?.getParameter(WebGLRenderingContext.RENDERER)?.let {
                add("Renderer" to it.toString())
            }
            gl?.getParameter(WebGLRenderingContext.VERSION)?.let {
                add("Version" to it.toString())
            }
        }
    }
}

