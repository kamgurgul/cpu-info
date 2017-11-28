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
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.LibraryGlideModule


/**
 * Module for application icons decoding based on:
 * https://gist.github.com/csshuai/9ccab646e35194eddafca9929cd9ad01
 *
 * @author kgurgul
 */
@GlideModule
class ApplicationIconModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(ApplicationInfo::class.java, ApplicationInfo::class.java,
                object : ModelLoaderFactory<ApplicationInfo, ApplicationInfo> {
                    override fun build(
                            multiFactory: MultiModelLoaderFactory):
                            ModelLoader<ApplicationInfo, ApplicationInfo> {
                        return ApplicationIconModelLoader()
                    }

                    override fun teardown() {
                    }
                })
                .append(ApplicationInfo::class.java, Drawable::class.java,
                        ApplicationIconDecoder(context))
    }
}