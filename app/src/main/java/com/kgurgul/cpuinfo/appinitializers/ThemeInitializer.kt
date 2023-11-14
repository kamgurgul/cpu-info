package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.kgurgul.cpuinfo.data.local.IUserPreferencesRepository
import com.kgurgul.cpuinfo.utils.ThemeHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ThemeInitializer @Inject constructor(
    private val userPreferencesRepository: IUserPreferencesRepository
) : AppInitializer {

    override fun init(application: Application) {
        runBlocking {
            ThemeHelper.applyTheme(userPreferencesRepository.userPreferencesFlow.first().theme)
        }
    }
}