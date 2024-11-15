package com.kgurgul.cpuinfo.domain.result

import com.kgurgul.cpuinfo.data.provider.IScreenDataProvider
import com.kgurgul.cpuinfo.domain.ImmutableInteractor
import com.kgurgul.cpuinfo.shared.Res
import com.kgurgul.cpuinfo.shared.orientation
import com.kgurgul.cpuinfo.utils.IDispatchersProvider
import com.kgurgul.cpuinfo.utils.resources.ILocalResources
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GetScreenDataInteractor(
    private val dispatchersProvider: IDispatchersProvider,
    private val localResources: ILocalResources,
    private val screenDataProvider: IScreenDataProvider
) : ImmutableInteractor<Unit, List<Pair<String, String>>>() {

    override val dispatcher: CoroutineDispatcher
        get() = dispatchersProvider.io

    private val initialDataFlow = flow {
        emit(screenDataProvider.getData())
    }

    override fun createObservable(params: Unit): Flow<List<Pair<String, String>>> {
        val orientationFlow = screenDataProvider.getOrientationFlow()
            .onStart { emit(INITIAL_ORIENTATION) }
            .map { orientation -> localResources.getString(Res.string.orientation) to orientation }
        return orientationFlow.flatMapLatest { orientation ->
            initialDataFlow.map { it + orientation }
        }
    }

    companion object {
        private const val INITIAL_ORIENTATION = "Unknown"
    }
}
