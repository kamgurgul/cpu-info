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

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping
import platform.Foundation.localizedCompare

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun smartCompare(a: String, b: String): Int {
    return (a as NSString).localizedCompare(b).toInt()
}

@Suppress("CAST_NEVER_SUCCEEDS")
actual fun String.normalize(): String {
    return (this as NSString).decomposedStringWithCanonicalMapping
}
