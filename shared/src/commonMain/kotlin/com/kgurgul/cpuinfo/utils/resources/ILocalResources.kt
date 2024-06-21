package com.kgurgul.cpuinfo.utils.resources

import org.jetbrains.compose.resources.StringResource

interface ILocalResources {

    suspend fun getString(resource: StringResource): String
}