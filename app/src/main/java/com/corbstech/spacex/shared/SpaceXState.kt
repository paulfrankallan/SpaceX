package com.corbstech.spacex.shared

import com.corbstech.spacex.feature.home.list.launch.LaunchItem
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import com.corbstech.spacex.shared.ui.list.RecyclerItem

sealed class Action {
    class Sort(val order: Order) : Action()
}

sealed class Order {
    object ASC : Order()
    object DESC : Order()
    object NONE : Order()
}

data class ViewSate(
    val filterMenuData: FilterMenuData = FilterMenuData(listOf()),
    val staticItems: List<RecyclerItem> = listOf(),
    val launchItems: List<LaunchItem> = listOf(),
) {
    fun getItems() = listOf(staticItems, launchItems).flatten()
}