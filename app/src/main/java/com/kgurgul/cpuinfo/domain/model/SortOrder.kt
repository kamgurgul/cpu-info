package com.kgurgul.cpuinfo.domain.model

enum class SortOrder {
    ASCENDING, DESCENDING, NONE
}

fun sortOrderFromBoolean(isAscending: Boolean): SortOrder =
    if (isAscending) SortOrder.ASCENDING else SortOrder.DESCENDING