package com.corbstech.spacex.feature.filtermenu

data class FilterMenuState(
    val sortOrder: SortOrder = SortOrder.None,
    val headerList: List<FilterMenuItem> = listOf(),
    val filterOptionMap: Map<FilterMenuItem, List<FilterMenuItem>> = mapOf(),
    val filtersApplied: Boolean = false
)

sealed class SortOrder {
    object Asc : SortOrder()
    object Desc : SortOrder()
    object None : SortOrder()
}

