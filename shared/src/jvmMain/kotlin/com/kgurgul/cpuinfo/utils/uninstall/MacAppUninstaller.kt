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
package com.kgurgul.cpuinfo.utils.uninstall

import com.kgurgul.cpuinfo.domain.model.UninstallOutcome
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.Files

internal fun canTrashMacBundle(
    file: File,
    runtimeHome: File = File(System.getProperty("java.home")),
): Boolean =
    try {
        val path = file.canonicalFile.toPath()
        file.isAbsolute &&
            !Files.isSymbolicLink(file.toPath()) &&
            file.extension.equals("app", ignoreCase = true) &&
            file.isDirectory &&
            File(file, "Contents/Info.plist").isFile &&
            !path.startsWith(File("/System").toPath()) &&
            !runtimeHome.canonicalFile.toPath().startsWith(path) &&
            path.parent?.let { Files.isWritable(it) } == true
    } catch (_: IOException) {
        false
    }

internal fun macTrashSupported(): Boolean =
    System.getProperty("cpuinfo.desktopUninstallEnabled", "true").toBoolean() &&
        Desktop.isDesktopSupported() &&
        Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH)

internal fun trashMacBundle(
    target: UninstallTarget.MacBundle,
    supported: Boolean = macTrashSupported(),
    moveToTrash: (File) -> Boolean = { Desktop.getDesktop().moveToTrash(it) },
): UninstallOutcome {
    val file = File(target.path)
    check(supported) { "Moving applications to Trash is unavailable" }
    require(canTrashMacBundle(file)) { "Application cannot be moved to Trash: ${target.path}" }
    if (!moveToTrash(file) || file.exists()) {
        throw IOException("Could not move application to Trash: ${target.path}")
    }
    return UninstallOutcome.REMOVED
}
