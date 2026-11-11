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
import com.kgurgul.cpuinfo.utils.uninstall.WindowsAppEntry
import com.kgurgul.cpuinfo.utils.uninstall.WindowsAppRegistry
import com.kgurgul.cpuinfo.utils.uninstall.WindowsUninstaller
import com.kgurgul.cpuinfo.utils.uninstall.windowsExitOutcome
import com.kgurgul.cpuinfo.utils.uninstall.windowsUninstallCommand
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class WindowsUninstallerTest {
    private val product = "{12345678-1234-1234-1234-123456789ABC}"
    private val target =
        UninstallTarget.WindowsRegistry(
            UninstallTarget.Hive.LOCAL_MACHINE,
            UninstallTarget.RegistryView.REGISTRY_64,
            product,
        )
    private val entry =
        WindowsAppEntry(target, "Example", uninstallCommand = "MsiExec.exe /I$product")

    @Test
    fun duplicateNamesKeepTheirRegistryIdentity() {
        val other = entry.copy(target = target.copy(hive = UninstallTarget.Hive.CURRENT_USER))
        val apps = listOf(entry, other).map { it.toApplicationData("") }
        assertNotEquals(apps[0].packageName, apps[1].packageName)
        assertEquals(target, apps[0].uninstallTarget)
        assertEquals(other.target, apps[1].uninstallTarget)
        assertEquals("Example", apps[0].displayIdentifier)
        assertNull(entry.copy(noRemove = true).toApplicationData("").uninstallTarget)
        assertNull(entry.copy(systemComponent = true).toApplicationData("").uninstallTarget)
        assertNull(entry.copy(uninstallCommand = null).toApplicationData("").uninstallTarget)
    }

    @Test
    fun msiMaintenanceBecomesInteractiveRemovalWithoutRestart() {
        val command = windowsUninstallCommand(entry, "C:\\Windows\\System32")
        assertEquals("C:\\Windows\\System32\\msiexec.exe", command.executable)
        assertEquals("/x $product /norestart", command.parameters)
        assertTrue(command.isMsi)
        assertEquals(
            command,
            windowsUninstallCommand(entry.copy(windowsInstaller = true), "C:\\Windows\\System32"),
        )
    }

    @Test
    fun registeredMsiUsesSelectedProductEvenWithoutAnUninstallString() {
        val command =
            windowsUninstallCommand(
                entry.copy(windowsInstaller = true, uninstallCommand = null),
                "C:\\Windows\\System32",
            )
        assertEquals("/x $product /norestart", command.parameters)
    }

    @Test
    fun msiAcceptsQuotedProductCodesAndOptionalExecutableSuffix() {
        for (command in listOf("msiexec /x \"$product\"", "MsiExec.exe/I$product")) {
            assertEquals(
                "/x $product /norestart",
                windowsUninstallCommand(
                    entry.copy(uninstallCommand = command),
                    "C:\\Windows\\System32",
                )
                    .parameters,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            windowsUninstallCommand(
                entry.copy(
                    windowsInstaller = true,
                    uninstallCommand = "MsiExec.exe /I$product MSIPATCHREMOVE={PATCH}",
                ),
                "C:\\Windows\\System32",
            )
        }
    }

    @Test
    fun executableAndArgumentsStaySeparate() {
        val executable = "C:\\Program Files\\Example & Co\\remove.exe"
        val parameters = "/uninstall /log \"C:\\My Logs\\remove.log\""
        for (path in listOf(executable, "\"$executable\"")) {
            val command =
                windowsUninstallCommand(
                    entry.copy(uninstallCommand = "$path $parameters"),
                    "unused",
                )
            assertEquals(executable, command.executable)
            assertEquals(parameters, command.parameters)
        }
        val expanded =
            windowsUninstallCommand(
                entry.copy(uninstallCommand = "\"%ProgramFiles%\\Example\\remove.exe\" /uninstall"),
                "unused",
            ) {
                it.replace("%ProgramFiles%", "C:\\Program Files")
            }
        assertEquals("C:\\Program Files\\Example\\remove.exe", expanded.executable)
    }

    @Test
    fun unsafeOrAmbiguousCommandsAreRejected() {
        for (command in
        listOf(
            "remove.exe /s",
            "cmd /c del something",
            "\"C:\\App\\remove.exe",
            "C:\\App\\remove.exe\nwhoami",
            "MsiExec.exe /I$product MSIPATCHREMOVE={PATCH}",
        )) {
            assertFailsWith<IllegalArgumentException> {
                windowsUninstallCommand(
                    entry.copy(uninstallCommand = command),
                    "C:\\Windows\\System32",
                )
            }
        }
    }

    @Test
    fun msiExitCodesDoNotBecomeGenericFailures() {
        assertEquals(UninstallOutcome.REMOVED, windowsExitOutcome(0, true))
        assertEquals(UninstallOutcome.CANCELLED, windowsExitOutcome(1602, true))
        assertEquals(UninstallOutcome.RESTART_REQUIRED, windowsExitOutcome(1641, true))
        assertEquals(UninstallOutcome.RESTART_REQUIRED, windowsExitOutcome(3010, true))
        assertFailsWith<IOException> { windowsExitOutcome(1603, true) }
        assertFailsWith<IOException> { windowsExitOutcome(3010, false) }
    }

    @Test
    fun verifiesOnlyTheSelectedRegistryEntryAndWaitsForHandoff() = runTest {
        val registry = FakeRegistry(entry)
        val uninstaller =
            WindowsUninstaller(registry) { selected ->
                assertEquals(entry, selected)
                launch {
                    delay(1_000)
                    registry.present = false
                }
                UninstallOutcome.REMOVED
            }
        assertEquals(UninstallOutcome.REMOVED, uninstaller.uninstall(target))
        assertTrue(registry.checkedTargets.all { it == target })
    }

    @Test
    fun zeroExitWithoutRemovalIsNotReportedAsSuccess() = runTest {
        val registry = FakeRegistry(entry)
        val uninstaller = WindowsUninstaller(registry) { UninstallOutcome.REMOVED }
        assertEquals(UninstallOutcome.UNCONFIRMED, uninstaller.uninstall(target))
        assertEquals(10_000L, testScheduler.currentTime)
    }

    @Test
    fun cancellationAndRestartArePreserved() = runTest {
        for (outcome in
        listOf(
            UninstallOutcome.CANCELLED,
            UninstallOutcome.RESTART_REQUIRED,
            UninstallOutcome.UNCONFIRMED,
        )) {
            val registry = FakeRegistry(entry)
            assertEquals(outcome, WindowsUninstaller(registry) { outcome }.uninstall(target))
            assertTrue(registry.checkedTargets.isEmpty())
        }
    }

    @Test
    fun missingEntryCannotLaunchAnUninstallerForASimilarlyNamedApp() = runTest {
        val registry = FakeRegistry(entry).apply { present = false }
        assertFailsWith<IOException> {
            WindowsUninstaller(registry) { error("Must not launch") }.uninstall(target)
        }
    }

    @Test
    fun cancellationStopsVerification() = runTest {
        val registry = FakeRegistry(entry)
        val job = launch {
            assertFailsWith<CancellationException> {
                WindowsUninstaller(registry) { UninstallOutcome.REMOVED }.uninstall(target)
            }
        }
        runCurrent()
        job.cancel()
        job.join()
        assertEquals(0L, testScheduler.currentTime)
    }

    private class FakeRegistry(private val entry: WindowsAppEntry) : WindowsAppRegistry {
        var present = true
        val checkedTargets = mutableListOf<UninstallTarget.WindowsRegistry>()

        override fun list() = if (present) listOf(entry) else emptyList()

        override fun read(target: UninstallTarget.WindowsRegistry) = entry.takeIf {
            present && target == it.target
        }

        override fun exists(target: UninstallTarget.WindowsRegistry): Boolean {
            checkedTargets.add(target)
            return present && target == entry.target
        }
    }
}
