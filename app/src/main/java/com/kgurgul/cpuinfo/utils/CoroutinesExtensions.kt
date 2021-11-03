package com.kgurgul.cpuinfo.utils

import com.kgurgul.cpuinfo.utils.wrappers.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Wrap passed [block] with [Result]. Flow will always start with [Result.InProgress] and finish
 * with [Result.Success] in case of success otherwise it will emit [Result.Error].
 */
fun <T> wrapToResultFlow(block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.InProgress)
    try {
        val result = block()
        emit(Result.Success(result))
    } catch (throwable: Throwable) {
        Timber.e(throwable)
        emit(Result.Error(throwable))
    }
}

/**
 * Create flow from suspended function wrapped with [Result]. Flow will always start with
 * [Result.InProgress] and finish with [Result.Success] in case of success otherwise it will emit
 * [Result.Error].
 */
fun <T> (suspend () -> T).asResultFlow(): Flow<Result<T>> = flow {
    emit(Result.InProgress)
    try {
        val result = invoke()
        emit(Result.Success(result))
    } catch (throwable: Throwable) {
        Timber.e(throwable)
        emit(Result.Error(throwable))
    }
}