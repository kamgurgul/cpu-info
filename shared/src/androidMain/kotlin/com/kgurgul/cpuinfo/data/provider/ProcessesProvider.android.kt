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

package com.kgurgul.cpuinfo.data.provider

import android.os.Build
import com.kgurgul.cpuinfo.domain.model.ProcessItem
import com.kgurgul.cpuinfo.utils.CpuLogger
import org.koin.core.annotation.Factory
import java.util.StringTokenizer

@Factory
actual class ProcessesProvider actual constructor() {

    actual fun areProcessesSupported() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.M

    actual fun getProcessList(): List<ProcessItem> {
        val psCmdList = readPsCmd()
        return parsePs(psCmdList)
    }

    private fun readPsCmd(): List<String> {
        return buildList {
            try {
                val args = arrayListOf("/system/bin/ps", "-p")
                val cmd = ProcessBuilder(args)
                val process = cmd.start()
                val bis = process.inputStream.bufferedReader()
                addAll(bis.readLines())
            } catch (e: Exception) {
                CpuLogger.e(e) { "Cannot read ps command" }
            }
        }
    }

    private fun parsePs(processLines: List<String>): List<ProcessItem> {
        val processItemList = ArrayList<ProcessItem>()
        processLines.forEachIndexed { i, line ->
            if (i > 0) {
                val st = StringTokenizer(line)
                var iterator = 0
                var user = ""
                var name = ""
                var pid = ""
                var ppid = ""
                var niceness = ""
                var rss = ""
                var vsize = ""

                while (st.hasMoreTokens()) {
                    when (iterator) {
                        USER_POSITION -> user = st.nextToken()
                        PID_POSITION -> pid = st.nextToken()
                        PPID_POSITION -> ppid = st.nextToken()
                        VSIZE_POSITION -> vsize = st.nextToken()
                        RSS_POSITION -> rss = st.nextToken()
                        NICENESS_POSITION -> niceness = st.nextToken()
                        NAME_POSITION -> name = st.nextToken()
                        else -> st.nextToken()
                    }
                    iterator++
                }

                processItemList.add(ProcessItem(name, pid, ppid, niceness, user, rss, vsize))
            }
        }
        return processItemList
    }

    companion object {
        private const val USER_POSITION = 0
        private const val PID_POSITION = 1
        private const val PPID_POSITION = 2
        private const val VSIZE_POSITION = 3
        private const val RSS_POSITION = 4
        private const val NICENESS_POSITION = 6
        private const val NAME_POSITION = 12
    }
}