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

package com.kgurgul.cpuinfo.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

/**
 * Helper class which handle ViewModel factory creation. Because of lazy initialization it is
 * possible to provide Dagger scopes. ViewModels lives in retained fragments so only way to separate
 * scopes is to provide dependencies via [ViewModelProvider.Factory].
 *
 * @author kgurgul
 */
class ViewModelInjectionFactory<VM : ViewModel> @Inject constructor(
        private val viewModel: dagger.Lazy<VM>) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
            viewModel.get() as T
}