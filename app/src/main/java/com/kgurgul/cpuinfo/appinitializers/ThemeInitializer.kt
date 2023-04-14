package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import android.content.SharedPreferences
import com.kgurgul.cpuinfo.utils.ThemeHelper
import javax.inject.Inject

/**
 * Apply theme on app start
 *
 * @author kgurgul
 */
class ThemeInitializer @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : AppInitializer {

    override fun init(application: Application) {
        sharedPreferences.getString(ThemeHelper.KEY_THEME, ThemeHelper.DEFAULT_MODE)?.let {
            ThemeHelper.applyTheme(it)
        }
    }
}