package com.kgurgul.cpuinfo.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Interactor {
    val dispatcher: CoroutineDispatcher
}

abstract class ImmutableInteractor<P : Any, T> : Interactor {

    protected abstract fun createObservable(params: P): Flow<T>

    fun observe(params: P): Flow<T> = createObservable(params).flowOn(dispatcher)
}

abstract class MutableInteractor<P : Any, T> : Interactor {

    private val sharedFlow = MutableSharedFlow<P>(replay = 1)

    operator fun invoke(params: P) = sharedFlow.tryEmit(params)

    protected abstract fun createObservable(params: P): Flow<T>

    fun observe(): Flow<T> = sharedFlow
        .flatMapLatest { createObservable(it).flowOn(dispatcher) }
}

abstract class ResultInteractor<in P, R> : Interactor {

    suspend operator fun invoke(params: P): R {
        return withContext(dispatcher) { doWork(params) }
    }

    protected abstract suspend fun doWork(params: P): R
}

fun <T> ImmutableInteractor<Unit, T>.observe() = observe(Unit)

operator fun <T> MutableInteractor<Unit, T>.invoke() = invoke(Unit)

fun <I : MutableInteractor<P, T>, P, T> CoroutineScope.launchObserve(
    interactor: I,
    f: suspend (Flow<T>) -> Unit
) = launch(interactor.dispatcher) { f(interactor.observe()) }

