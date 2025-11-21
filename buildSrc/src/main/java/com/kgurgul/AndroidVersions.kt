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
package com.kgurgul

object AndroidVersions {

    const val MIN_SDK = 23
    const val WEAR_MIN_SDK = 26
    const val TARGET_SDK = 36
    const val COMPILE_SDK = 36

    private const val VERSION_MAJOR = 6
    private const val VERSION_MINOR = 5
    private const val VERSION_PATCH = 0

    const val VERSION_CODE = VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    const val WEAR_VERSION_CODE =
        1000000 + VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"

    const val NDK_VERSION = "28.2.13676358"
}
