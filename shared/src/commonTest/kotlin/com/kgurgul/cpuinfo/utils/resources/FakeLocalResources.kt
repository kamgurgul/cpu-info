package com.kgurgul.cpuinfo.utils.resources

import org.jetbrains.compose.resources.StringResource

class FakeLocalResources(
    private val stringValue: String = "Test"
) : ILocalResources {

    override suspend fun getString(resource: StringResource): String {
        return stringValue
    }
}
