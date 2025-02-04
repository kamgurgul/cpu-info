package com.kgurgul.cpuinfo.utils.navigation

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class TopLevelRoute<T : Any>(
    val name: StringResource,
    val route: T,
    val icon: DrawableResource
)
