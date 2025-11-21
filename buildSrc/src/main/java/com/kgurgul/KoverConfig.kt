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

object KoverConfig {

    val includedPackages = listOf("com.kgurgul.cpuinfo.*")
    val excludedPackages =
        listOf(
            "com.kgurgul.cpuinfo.widgets",
            "com.kgurgul.cpuinfo.ui",
            "com.kgurgul.cpuinfo.*.model*",
            "com.kgurgul.cpuinfo.*.models*",
        )
    val excludedClasses =
        listOf(
            "*Module*",
            "com.kgurgul.cpuinfo.*Directions*",
            "*MembersInjector*",
            "*_Companion_*",
            "*_Factory*",
        )
    val excludedAnnotations = "*Generated*"
}
