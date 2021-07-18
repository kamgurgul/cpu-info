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

package com.kgurgul.cpuinfo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

/**
 * General utility class
 *
 * @author kgurgul
 */
object Utils {

    /**
     * Helper which adds item to list if value is not null and not empty
     */
    fun addPairIfExists(list: MutableList<Pair<String, String>>, key: String, value: String?) {
        if (value != null && value.isNotEmpty()) {
            list.add(Pair(key, value))
        }
    }

    /**
     * Convert bytes into normalized unit string
     */
    fun humanReadableByteCount(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return "$bytes B"
        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format(Locale.US, "%.2f %sB", bytes / unit.toDouble().pow(exp.toDouble()), pre)
    }

    /**
     * Format passed bytes into megabytes string
     */
    fun convertBytesToMega(bytes: Long): String {
        val megaBytes = bytes.toDouble() / (1024.0 * 1024.0)
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

        return "${df.format(megaBytes)} MB"
    }

    /**
     * Open google with passed query
     */
    fun searchInGoogle(context: Context, query: String) {
        val uri = Uri.parse("http://www.google.com/search?q=$query")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    /**
     * Read and parse to Long first line from file
     */
    fun readOneLine(file: File): Double? {
        val text: String?
        try {
            val fs = FileInputStream(file)
            val sr = InputStreamReader(fs)
            val br = BufferedReader(sr)
            text = br.readLine()
            br.close()
            sr.close()
            fs.close()
        } catch (ex: Exception) {
            return null
        }

        val value: Double?
        try {
            value = text.toDouble()
        } catch (nfe: NumberFormatException) {
            return null
        }

        return value
    }
}