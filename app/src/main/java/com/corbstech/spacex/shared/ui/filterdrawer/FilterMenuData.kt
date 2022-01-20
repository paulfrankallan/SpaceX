package com.corbstech.spacex.shared.ui.filterdrawer

import java.util.*

data class FilterMenuData(
    val headerList: List<FilterMenuItem>,
    val childList: HashMap<FilterMenuItem, List<String>> = hashMapOf()
)