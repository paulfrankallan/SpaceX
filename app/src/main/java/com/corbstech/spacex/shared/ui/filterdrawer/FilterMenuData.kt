package com.corbstech.spacex.shared.ui.filterdrawer

import com.corbstech.spacex.shared.SortOrder

data class FilterMenuData(
    val sortOrder: SortOrder = SortOrder.None,
    val headerList: List<FilterMenuItem> = listOf(),
    val filterOptionMap: Map<FilterMenuItem, List<FilterMenuItem>> = mapOf(),
    val filtersApplied: Boolean = false
)

