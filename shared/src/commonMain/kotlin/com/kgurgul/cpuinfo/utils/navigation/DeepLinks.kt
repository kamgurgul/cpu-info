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
package com.kgurgul.cpuinfo.utils.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import kotlinx.serialization.KSerializer

/** Creates a matcher for the given [path] which is relative to [NavigationConst.BASE_URL]. */
fun <T : NavKey> deepLinkMatcher(path: String, serializer: KSerializer<T>): UriDeepLinkMatcher<T> =
    UriDeepLinkMatcher(
        uriPattern = DeepLinkUri(NavigationConst.BASE_URL + path),
        serializer = serializer,
    )

/** Returns the route which is the best match for the [uri] or null if nothing matches. */
fun List<DeepLinkMatcher<NavKey, *>>.findRoute(uri: String?): NavKey? {
    if (uri.isNullOrBlank()) return null
    return try {
        val request = DeepLinkRequest(uri = uri)
        mapNotNull { it.match(request) }.maxOrNull()?.key
    } catch (_: Exception) {
        null
    }
}
