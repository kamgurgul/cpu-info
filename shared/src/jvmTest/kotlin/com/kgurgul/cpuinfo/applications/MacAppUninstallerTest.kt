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
package com.kgurgul.cpuinfo.applications

import com.kgurgul.cpuinfo.domain.model.UninstallOutcome
import com.kgurgul.cpuinfo.domain.model.UninstallTarget
import com.kgurgul.cpuinfo.utils.uninstall.canTrashMacBundle
import com.kgurgul.cpuinfo.utils.uninstall.trashMacBundle
import com.sun.jna.Platform
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Assume.assumeFalse

class MacAppUninstallerTest {
    @Test
    fun pathsWithQuotesAndBackslashesAreValidatedAsFiles() = withDirectory { directory ->
        val bundle = bundle(directory, "Example \"Beta\"\\name.app")
        assertTrue(canTrashMacBundle(bundle))
        assertFalse(canTrashMacBundle(bundle, File(bundle, "Contents/runtime/Contents/Home")))
    }

    @Test
    fun arbitraryDirectoriesMissingBundlesAndSymlinksAreRejected() = withDirectory { directory ->
        val arbitrary = File(directory, "Documents").apply { mkdirs() }
        assertFalse(canTrashMacBundle(arbitrary))
        val missingPlist = File(directory, "Missing.app").apply { mkdirs() }
        assertFalse(canTrashMacBundle(missingPlist))
        val bundle = bundle(directory, "Example.app")
        val link = File(directory, "Alias.app").toPath()
        Files.createSymbolicLink(link, bundle.toPath())
        assertFalse(canTrashMacBundle(link.toFile()))
        assertFalse(canTrashMacBundle(File("relative.app")))
    }

    @Test
    fun nativeTrashReceivesTheExactPathAndMustReportSuccess() = withDirectory { directory ->
        val bundle = bundle(directory, "Example \"Beta\"\\name.app")
        val target = UninstallTarget.MacBundle(bundle.absolutePath)
        assertFailsWith<IOException> { trashMacBundle(target, supported = true) { false } }
        assertFailsWith<IOException> { trashMacBundle(target, supported = true) { true } }
        assertEquals(
            UninstallOutcome.REMOVED,
            trashMacBundle(target, supported = true) { file ->
                assertEquals(bundle.absolutePath, file.absolutePath)
                file.renameTo(File(directory, "Moved.app"))
            },
        )
    }

    private fun bundle(directory: File, name: String): File =
        File(directory, name).apply {
            File(this, "Contents").mkdirs()
            File(this, "Contents/Info.plist").writeText("<plist/>")
        }

    private fun withDirectory(test: (File) -> Unit) {
        assumeFalse(Platform.isWindows())
        val directory = Files.createTempDirectory("cpuinfo-uninstall-test").toFile()
        try {
            test(directory)
        } finally {
            directory.deleteRecursively()
        }
    }
}
