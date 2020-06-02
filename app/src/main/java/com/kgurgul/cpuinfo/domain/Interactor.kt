package com.kgurgul.cpuinfo.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

interface ObservableInteractor<T> {
    val dispatcher: CoroutineDispatcher
    fun observe(): Flow<T>
}

abstract class SubjectInteractor<P : Any, T> : ObservableInteractor<T> {

    private val channel = ConflatedBroadcastChannel<P>()

    operator fun invoke(params: P) = channel.sendBlocking(params)

    protected abstract fun createObservable(params: P): Flow<T>

    override fun observe(): Flow<T> = channel.asFlow()
            .distinctUntilChanged()
            .flatMapLatest { createObservable(it) }
}

operator fun <T> SubjectInteractor<Unit, T>.invoke() = invoke(Unit)

fun <I : ObservableInteractor<T>, T> CoroutineScope.launchObserve(
        interactor: I,
        f: suspend (Flow<T>) -> Unit
) = launch(interactor.dispatcher) { f(interactor.observe()) }

fun <I : SubjectInteractor<Unit, T>, T> CoroutineScope.launchObserve(
        interactor: I,
        f: suspend (Flow<T>) -> Unit
) = launch(interactor.dispatcher) { f(interactor.observe()) }.also { interactor() }
