package com.kgurgul.cpuinfo.utils.wrappers

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val throwable: Throwable) : Result<Nothing>()
    object InProgress : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[throwable=$throwable]"
            InProgress -> "InProgress"
        }
    }

    /**
     * This function combines status only of two results into one. This is useful when only
     * status is needed (InProgress/Success/Error) but nothing else is needed.
     */
    fun <S> combineStatusOnlyWith(secondResult: Result<S>): Result<Unit> {
        return when {
            this is Success && secondResult is Success -> Success(Unit)
            this is InProgress || secondResult is InProgress -> InProgress
            else -> Error(ResultCombinationException())
        }
    }

    class ResultCombinationException : Exception()
}
