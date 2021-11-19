package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.R
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.utils.wrappers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewApplicationsViewModel @Inject constructor(
    private val applicationsDataObservable: ApplicationsDataObservable
) : ViewModel() {

    val applicationList = applicationsDataObservable.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        refreshApplications()
    }

    fun refreshApplications() {
        applicationsDataObservable.invoke(
            ApplicationsDataObservable.Params(
                withSystemApps = false,
                sortOrderFromBoolean(true)
            )
        )
    }

    fun onApplicationClicked(packageName: String) {
        viewModelScope.launch {
            eventChannel.send(Event.ShowSnackbar(R.string.cpu_open))
        }
        Timber.d("App clicked: %s", packageName)
    }

    sealed class Event {
        data class ShowSnackbar(val messageRes: Int) : Event()
    }
}