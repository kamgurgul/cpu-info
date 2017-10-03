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

package com.kgurgul.cpuinfo.features.applications

import android.os.Parcel
import android.os.Parcelable

/**
 * Model for [ApplicationsAdapter]
 *
 * @author kgurgul
 */
data class ExtendedAppInfo(val name: String,
                           val packageName: String,
                           val nativeLibraryDir: String?,
                           var appSize: Long = 0) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(packageName)
        writeString(nativeLibraryDir)
        writeLong(appSize)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ExtendedAppInfo> = object : Parcelable.Creator<ExtendedAppInfo> {
            override fun createFromParcel(source: Parcel): ExtendedAppInfo = ExtendedAppInfo(source)
            override fun newArray(size: Int): Array<ExtendedAppInfo?> = arrayOfNulls(size)
        }
    }
}