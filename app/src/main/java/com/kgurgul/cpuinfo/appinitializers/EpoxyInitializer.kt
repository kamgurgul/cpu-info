package com.kgurgul.cpuinfo.appinitializers

import android.app.Application
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import javax.inject.Inject

class EpoxyInitializer @Inject constructor() : AppInitializer {

    override fun init(application: Application) {
        val asyncHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
        EpoxyController.defaultDiffingHandler = asyncHandler
    }
}