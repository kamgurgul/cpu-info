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
package com.kgurgul.cpuinfo.domain.model

/** Identity of the installed app, never an executable command supplied by the UI. */
sealed interface UninstallTarget {
    data class WindowsRegistry(
        val hive: Hive,
        val view: RegistryView,
        val key: String,
    ) : UninstallTarget {
        val id: String
            get() = "${hive.name}/${view.name}/$key"
    }

    data class MacBundle(val path: String) : UninstallTarget

    enum class Hive {
        LOCAL_MACHINE,
        CURRENT_USER,
    }

    enum class RegistryView {
        REGISTRY_64,
        REGISTRY_32,
    }
}

enum class UninstallOutcome {
    REMOVED,
    CANCELLED,
    RESTART_REQUIRED,

    /** The installer was launched, but removal has not been verified. */
    UNCONFIRMED,
}
