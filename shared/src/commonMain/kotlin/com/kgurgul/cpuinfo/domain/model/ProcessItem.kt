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

package com.kgurgul.cpuinfo.domain.model

import com.kgurgul.cpuinfo.utils.smartCompare

data class ProcessItem(
    val name: String,
    val pid: String,
    val ppid: String,
    val niceness: String,
    val user: String,
    val rss: String,
    val vsize: String,
) : Comparable<ProcessItem> {

    override fun compareTo(other: ProcessItem): Int {
        return smartCompare(name, other.name)
    }
}
