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
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.imageio.ImageIO
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

/**
 * Custom Coil decoder for macOS .icns icon files.
 *
 * Uses Java ImageIO with TwelveMonkeys ImageIO plugin to decode ICNS files. TwelveMonkeys is
 * automatically registered via Java's Service Provider Interface (SPI), so ImageIO.read() will
 * handle .icns files once the library is on the classpath.
 *
 * The decoded BufferedImage is then converted to Skia Bitmap for display in Compose.
 */
class MacOSIconDecoder(
    private val source: ImageSource,
    @Suppress("unused") private val options: Options,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val file = source.file().toFile()
        val bufferedImage =
            ImageIO.read(file)
                ?: throw IllegalStateException(
                    "Failed to decode ICNS file: ${file.absolutePath}. " +
                        "Ensure TwelveMonkeys imageio-icns is on the classpath."
                )
        val skiaBitmap = bufferedImageToSkiaBitmap(bufferedImage)
        return DecodeResult(image = skiaBitmap.asImage(), isSampled = false)
    }

    private fun bufferedImageToSkiaBitmap(bufferedImage: BufferedImage): Bitmap {
        val width = bufferedImage.width
        val height = bufferedImage.height

        // Get pixel data from BufferedImage
        val pixels = IntArray(width * height)
        bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)

        // Convert ARGB to RGBA bytes (Skia format)
        val byteBuffer = ByteBuffer.allocate(width * height * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (argb in pixels) {
            val a = (argb shr 24) and 0xFF
            val r = (argb shr 16) and 0xFF
            val g = (argb shr 8) and 0xFF
            val b = argb and 0xFF
            // RGBA order
            byteBuffer.put(r.toByte())
            byteBuffer.put(g.toByte())
            byteBuffer.put(b.toByte())
            byteBuffer.put(a.toByte())
        }

        val bytes = byteBuffer.array()

        // Create Skia Bitmap
        return Bitmap().apply {
            allocPixels(
                ImageInfo(
                    width = width,
                    height = height,
                    colorType = ColorType.RGBA_8888,
                    alphaType = ColorAlphaType.UNPREMUL,
                )
            )
            installPixels(bytes)
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            val file = result.source.file().toFile()

            val isMacOS =
                System.getProperty("os.name").contains("mac", ignoreCase = true) ||
                    System.getProperty("os.name").contains("darwin", ignoreCase = true)

            return if (isMacOS && file.extension == "icns") {
                MacOSIconDecoder(result.source, options)
            } else {
                null
            }
        }
    }
}
