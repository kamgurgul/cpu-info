package com.kgurgul.cpuinfo.utils.resources

import org.jetbrains.compose.resources.StringResource

class LocalResources : ILocalResources {

    override suspend fun getString(resource: StringResource): String =
        org.jetbrains.compose.resources.getString(resource)
}
