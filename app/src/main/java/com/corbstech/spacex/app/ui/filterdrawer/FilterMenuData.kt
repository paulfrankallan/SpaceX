package com.corbstech.spacex.app.ui.filterdrawer

import com.corbstech.spacex.app.SortOrder

data class FilterMenuData(
    val sortOrder: SortOrder = SortOrder.None,
    val headerList: List<FilterMenuItem> = listOf(),
    val filterOptionMap: Map<FilterMenuItem, List<FilterMenuItem>> = mapOf(),
    val filtersApplied: Boolean = false
)

