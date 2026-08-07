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
package com.kgurgul.cpuinfo.utils

import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.Guid.GUID
import com.sun.jna.platform.win32.Guid.IID
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.WinDef.HBITMAP
import com.sun.jna.platform.win32.WinGDI
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.platform.win32.WinNT.HRESULT
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions
import java.nio.ByteBuffer
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/**
 * Custom Coil decoder for Windows .exe files to extract embedded icons.
 *
 * Uses JNA to call Windows Shell32.dll IShellItemImageFactory API to extract icons from executable
 * files. The extracted bitmap is converted to a Skia Bitmap for display in Compose.
 */
class WindowsExeIconDecoder(
    private val source: ImageSource,
    @Suppress("unused") private val options: Options,
) : Decoder {

    companion object {
        private const val ICON_SIZE = 48
        // SIIGBF_ICONONLY flag - only get the icon, not the thumbnail
        private const val SIIGBF_ICONONLY = 0x00000004
    }

    override suspend fun decode(): DecodeResult? {
        val file = source.file().toFile()
        return try {
            val bitmap = extractIconBitmap(file.absolutePath)
            bitmap?.let { DecodeResult(image = it.asImage(), isSampled = false) }
        } catch (e: Exception) {
            CpuLogger.e(e) { "Error decoding icon from: ${file.absolutePath}" }
            null
        }
    }

    private fun extractIconBitmap(filePath: String): Bitmap? {
        val file = java.io.File(filePath)
        if (!file.exists()) {
            return null
        }

        // Initialize COM
        val hrInit = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_APARTMENTTHREADED)
        if (
            hrInit.toLong() != 0L && hrInit.toLong() != 1L
        ) { // S_OK or S_FALSE (already initialized)
            CpuLogger.e { "Failed to initialize COM: ${hrInit.toLong()}" }
            return null
        }

        try {
            return extractUsingShellItemImageFactory(filePath)
        } finally {
            Ole32.INSTANCE.CoUninitialize()
        }
    }

    private fun extractUsingShellItemImageFactory(filePath: String): Bitmap? {
        val ppv = PointerByReference()
        val hr =
            Shell32Ex.INSTANCE.SHCreateItemFromParsingName(
                filePath,
                Pointer.NULL,
                IShellItemImageFactory.IID_IShellItemImageFactory,
                ppv,
            )

        if (COMUtils.FAILED(hr)) {
            CpuLogger.e { "SHCreateItemFromParsingName failed: ${hr.toLong()}" }
            return null
        }

        val shellItemImageFactory = IShellItemImageFactory(ppv.value)
        try {
            val hbitmapRef = PointerByReference()

            val hrGetImage =
                shellItemImageFactory.GetImage(ICON_SIZE, ICON_SIZE, SIIGBF_ICONONLY, hbitmapRef)
            if (COMUtils.FAILED(hrGetImage)) {
                CpuLogger.e { "GetImage failed: ${hrGetImage.toLong()}" }
                return null
            }

            val hBitmap = HBITMAP(hbitmapRef.value)
            try {
                return hBitmapToSkiaBitmap(hBitmap)
            } finally {
                GDI32.INSTANCE.DeleteObject(hBitmap)
            }
        } finally {
            shellItemImageFactory.Release()
        }
    }

    private fun hBitmapToSkiaBitmap(hBitmap: HBITMAP): Bitmap? {
        // Get bitmap info
        val bitmapInfo = WinGDI.BITMAP()
        bitmapInfo.write()
        val getObjectResult =
            GDI32.INSTANCE.GetObjectW(hBitmap, bitmapInfo.size(), bitmapInfo.pointer)
        if (getObjectResult == 0) {
            CpuLogger.e { "Failed to get bitmap object" }
            return null
        }
        bitmapInfo.read()

        val width = bitmapInfo.bmWidth.toInt()
        val height = bitmapInfo.bmHeight.toInt()

        if (width <= 0 || height <= 0) {
            CpuLogger.e { "Invalid bitmap dimensions: ${width}x${height}" }
            return null
        }

        // Create a compatible DC
        val hdcScreen = User32.INSTANCE.GetDC(null)
        val hdcMem = GDI32.INSTANCE.CreateCompatibleDC(hdcScreen)
        val hbmOld = GDI32.INSTANCE.SelectObject(hdcMem, hBitmap)

        try {
            // Get the bitmap bits
            val bi = WinGDI.BITMAPINFO()
            bi.bmiHeader.biSize = bi.bmiHeader.size()
            bi.bmiHeader.biWidth = width
            bi.bmiHeader.biHeight = -height // Top-down DIB
            bi.bmiHeader.biPlanes = 1
            bi.bmiHeader.biBitCount = 32
            bi.bmiHeader.biCompression = WinGDI.BI_RGB

            val bufferSize = width * height * 4
            val buffer = ByteArray(bufferSize)

            GDI32.INSTANCE.GetDIBits(hdcMem, hBitmap, 0, height, buffer, bi, WinGDI.DIB_RGB_COLORS)

            // Convert BGRA to proper format for Skia
            // The buffer is in BGRA format, we need to ensure alpha is correct
            val byteBuffer = ByteBuffer.allocate(bufferSize)
            var offset = 0
            repeat(height) {
                repeat(width) {
                    val b = buffer[offset].toInt() and 0xFF
                    val g = buffer[offset + 1].toInt() and 0xFF
                    val r = buffer[offset + 2].toInt() and 0xFF
                    var a = buffer[offset + 3].toInt() and 0xFF

                    // If alpha is 0 but we have color data, assume fully opaque
                    if (a == 0 && (r != 0 || g != 0 || b != 0)) {
                        a = 255
                    }

                    // BGRA order for Windows/Skia
                    byteBuffer.put(b.toByte())
                    byteBuffer.put(g.toByte())
                    byteBuffer.put(r.toByte())
                    byteBuffer.put(a.toByte())

                    offset += 4
                }
            }

            val bytes = byteBuffer.array()

            return Bitmap().apply {
                allocPixels(
                    ImageInfo(
                        width = width,
                        height = height,
                        colorType = ColorType.BGRA_8888,
                        alphaType = ColorAlphaType.UNPREMUL,
                    )
                )
                installPixels(bytes)
            }
        } finally {
            GDI32.INSTANCE.SelectObject(hdcMem, hbmOld)
            GDI32.INSTANCE.DeleteDC(hdcMem)
            User32.INSTANCE.ReleaseDC(null, hdcScreen)
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            if (!Platform.isWindows()) {
                return null
            }

            return try {
                val file = result.source.file().toFile()
                if (file.extension.equals("exe", ignoreCase = true)) {
                    WindowsExeIconDecoder(result.source, options)
                } else {
                    null
                }
            } catch (e: Exception) {
                CpuLogger.e(e) { "Error in WindowsExeIconDecoder.Factory.create()" }
                null
            }
        }
    }

    // IShellItemImageFactory COM interface
    class IShellItemImageFactory(private val pointer: Pointer) {
        private val vtbl: Pointer = pointer.getPointer(0)

        companion object {
            val IID_IShellItemImageFactory: IID = IID("{bcc18b79-ba16-442f-80c4-8a59c30c463b}")
        }

        fun GetImage(width: Int, height: Int, flags: Int, phbm: PointerByReference): HRESULT {
            // GetImage is at vtable index 3 (after QueryInterface, AddRef, Release)
            val func =
                com.sun.jna.Function.getFunction(vtbl.getPointer(3 * Native.POINTER_SIZE.toLong()))
            // On x64 Windows, SIZE structure (8 bytes) is passed as a single 64-bit value
            // with cx in the lower 32 bits and cy in the upper 32 bits
            val sizeAsLong =
                (width.toLong() and 0xFFFFFFFFL) or ((height.toLong() and 0xFFFFFFFFL) shl 32)
            val args = arrayOf(pointer, sizeAsLong, flags, phbm)
            return HRESULT(func.invokeInt(args))
        }

        fun Release(): Int {
            val func =
                com.sun.jna.Function.getFunction(vtbl.getPointer(2 * Native.POINTER_SIZE.toLong()))
            return func.invokeInt(arrayOf(pointer))
        }
    }

    // Extended Shell32 interface
    interface Shell32Ex : StdCallLibrary {
        companion object {
            val INSTANCE: Shell32Ex =
                Native.load("shell32", Shell32Ex::class.java, W32APIOptions.DEFAULT_OPTIONS)
                    as Shell32Ex
        }

        fun SHCreateItemFromParsingName(
            pszPath: String,
            pbc: Pointer?,
            riid: GUID,
            ppv: PointerByReference,
        ): HRESULT
    }

    // JNA interface for User32
    interface User32 : StdCallLibrary {
        companion object {
            val INSTANCE: User32 = Native.load("user32", User32::class.java) as User32
        }

        fun GetDC(hWnd: HANDLE?): WinDef.HDC

        fun ReleaseDC(hWnd: HANDLE?, hDC: WinDef.HDC): Int
    }

    // JNA interface for GDI32
    interface GDI32 : StdCallLibrary {
        companion object {
            val INSTANCE: GDI32 = Native.load("gdi32", GDI32::class.java) as GDI32
        }

        fun GetObjectW(h: HANDLE, c: Int, pv: Pointer): Int

        fun CreateCompatibleDC(hdc: WinDef.HDC?): WinDef.HDC

        fun SelectObject(hdc: WinDef.HDC, h: HANDLE): HANDLE

        fun DeleteObject(ho: HANDLE): Boolean

        fun DeleteDC(hdc: WinDef.HDC): Boolean

        fun GetDIBits(
            hdc: WinDef.HDC,
            hbm: HANDLE,
            start: Int,
            cLines: Int,
            lpvBits: ByteArray,
            lpbmi: WinGDI.BITMAPINFO,
            usage: Int,
        ): Int
    }

    // Helper class for Windows types
    object WinDef {
        class HDC : HANDLE()
    }
}
