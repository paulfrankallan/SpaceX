package com.corbstech.spacex.shared

import com.corbstech.spacex.feature.home.list.launch.LaunchItem
import com.corbstech.spacex.feature.home.list.launch.LaunchItemLink
import com.corbstech.spacex.shared.ui.filterdrawer.FilterMenuData
import com.corbstech.spacex.shared.ui.list.RecyclerItem
import java.util.*

sealed class Action {
    class Sort(val order: Order) : Action()
    class LaunchItemLinkClicked(val launchItemLink: LaunchItemLink) : Action()
}

sealed class Order {
    object ASC : Order()
    object DESC : Order()
    object NONE : Order()
}

sealed class Event {
    val uniqueId: String = UUID.randomUUID().toString()
    class LaunchWebBrowser(val url: String) : Event()
}

data class ViewSate(
    val filterMenuData: FilterMenuData = FilterMenuData(listOf()),
    val staticItems: List<RecyclerItem> = listOf(),
    val launchItems: List<LaunchItem> = listOf(),
    val events: List<Event> = emptyList()
) {
    fun getItems() = listOf(staticItems, launchItems).flatten()
}