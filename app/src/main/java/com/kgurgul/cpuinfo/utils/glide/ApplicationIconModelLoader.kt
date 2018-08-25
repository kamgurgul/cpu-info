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

import android.content.pm.ApplicationInfo
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey


/**
 * Loader for application icons decoder module based on:
 * https://gist.github.com/csshuai/9ccab646e35194eddafca9929cd9ad01
 *
 * @author kgurgul
 */
class ApplicationIconModelLoader : ModelLoader<ApplicationInfo, ApplicationInfo> {
    override fun buildLoadData(model: ApplicationInfo, width: Int, height: Int, options: Options):
            ModelLoader.LoadData<ApplicationInfo>? {
        return ModelLoader.LoadData(ObjectKey(model),
                object : DataFetcher<ApplicationInfo> {
                    override fun loadData(priority: Priority,
                                          callback: DataFetcher.DataCallback<in ApplicationInfo>) {
                        callback.onDataReady(model)
                    }

                    override fun cleanup() {
                    }

                    override fun cancel() {
                    }

                    override fun getDataClass(): Class<ApplicationInfo> =
                            ApplicationInfo::class.java

                    override fun getDataSource(): DataSource = DataSource.LOCAL
                })
    }

    override fun handles(model: ApplicationInfo) = true
}