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
package com.kgurgul.cpuinfo.data.provider

class FakeCpuDataNativeProvider : ICpuDataNativeProvider {

    override fun initLibrary() {}

    override fun getCpuName(): String {
        return "CPU_NAME"
    }

    override fun hasArmNeon(): Boolean {
        return true
    }

    override fun getL1dCaches(): IntArray? {
        return null
    }

    override fun getL1iCaches(): IntArray? {
        return null
    }

    override fun getL2Caches(): IntArray? {
        return null
    }

    override fun getL3Caches(): IntArray? {
        return null
    }

    override fun getL4Caches(): IntArray? {
        return null
    }

    override fun getNumberOfCores(): Int {
        return 1
    }
}
