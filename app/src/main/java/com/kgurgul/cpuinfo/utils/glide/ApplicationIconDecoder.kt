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

package com.kgurgul.cpuinfo.utils.glide

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.bumptech.glide.util.Util


/**
 * Decoder for application icons based on:
 * https://gist.github.com/csshuai/9ccab646e35194eddafca9929cd9ad01
 *
 * @author kgurgul
 */
class ApplicationIconDecoder(private val context: Context) :
        ResourceDecoder<ApplicationInfo, Drawable> {

    override fun decode(source: ApplicationInfo, width: Int, height: Int, options: Options):
            Resource<Drawable> {
        val icon = source.loadIcon(context.packageManager)
        return object : DrawableResource<Drawable>(icon) {
            override fun getResourceClass(): Class<Drawable> {
                return Drawable::class.java
            }

            override fun getSize(): Int {
                return if (drawable is BitmapDrawable) {
                    Util.getBitmapByteSize(drawable.bitmap)
                } else {
                    1
                }
            }

            override fun recycle() {
            }
        }
    }

    override fun handles(source: ApplicationInfo, options: Options): Boolean =
            true
}