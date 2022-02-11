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

package com.kgurgul.cpuinfo.features.processes

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals

/**
 * Tests for [PsProvider]
 *
 * @author kgurgul
 */
@RunWith(MockitoJUnitRunner::class)
class PsProviderTest {

    @Test
    fun parsePs() {
        /* Given */
        val psProvider = PsProvider()
        val generatedProcessItems = getProcessItemsList()

        /* When */
        val parsedProcessItems = psProvider.parsePs(getDummyPsOutput())

        /* Then */
        assertEquals(generatedProcessItems.size, parsedProcessItems.size)
        assertEquals(generatedProcessItems[0], parsedProcessItems[0])
        assertEquals(generatedProcessItems[1], parsedProcessItems[1])
    }

    /**
     * Generates sample output from ps. It should produce items from [getProcessItemsList] after
     * parsing
     */
    private fun getDummyPsOutput(): List<String> {
        val psStringList = ArrayList<String>()
        psStringList.add("USER NAME     PID   PPID  VSIZE  RSS   PRIO  NICE  RTPRI SCHED   " +
                "WCHAN    PC")
        psStringList.add("root      1     0     724    444   20    0     0     0     c02cb2ef " +
                "0805d376 S /init")
        psStringList.add("root      2     1     0      0     20    2     0     0     c023edb3 " +
                "00000000 S kthreadd")
        return psStringList
    }

    /**
     * Return generated [List] of [ProcessItem]
     */
    private fun getProcessItemsList(): List<ProcessItem> {
        val processItems = ArrayList<ProcessItem>()
        val p1 = ProcessItem("/init", "1", "0", "0", "root", "444", "724")
        val p2 = ProcessItem("kthreadd", "2", "1", "2", "root", "0", "0")
        processItems.add(p1)
        processItems.add(p2)
        return processItems
    }
}