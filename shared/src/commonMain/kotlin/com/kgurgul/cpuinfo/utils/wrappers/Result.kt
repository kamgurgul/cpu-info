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
package com.kgurgul.cpuinfo.utils.wrappers

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val throwable: Throwable) : Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[throwable=$throwable]"
            Loading -> "Loading"
        }
    }

    /**
     * This function combines status only of two results into one. This is useful when only status
     * is needed (Loading/Success/Error) but nothing else is needed.
     */
    fun <S> combineStatusOnlyWith(secondResult: Result<S>): Result<Unit> {
        return when {
            this is Success && secondResult is Success -> Success(Unit)
            this is Loading || secondResult is Loading -> Loading
            else -> Error(ResultCombinationException())
        }
    }

    class ResultCombinationException : Exception()
}
