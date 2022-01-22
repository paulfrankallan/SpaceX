package com.corbstech.spacex.shared.ui.filterdrawer

import com.corbstech.spacex.shared.Order
import java.util.*

data class FilterMenuData(
    val order: Order = Order.None,
    val headerList: List<FilterMenuItem> = listOf(),
    val childList: HashMap<FilterMenuItem, List<FilterMenuItem>> = hashMapOf()
)

