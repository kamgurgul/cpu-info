package com.kgurgul.cpuinfo.features.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kgurgul.cpuinfo.domain.model.sortOrderFromBoolean
import com.kgurgul.cpuinfo.domain.observable.ApplicationsDataObservable
import com.kgurgul.cpuinfo.utils.wrappers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NewApplicationsViewModel @Inject constructor(
    private val applicationsDataObservable: ApplicationsDataObservable
) : ViewModel() {

    val applicationList = applicationsDataObservable.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading)

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
}