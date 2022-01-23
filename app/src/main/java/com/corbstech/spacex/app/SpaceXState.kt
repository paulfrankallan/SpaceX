package com.corbstech.spacex.app

import com.corbstech.spacex.feature.list.launch.LaunchItem
import com.corbstech.spacex.feature.list.launch.LaunchItemLink
import com.corbstech.spacex.app.ui.filterdrawer.FilterMenuData
import com.corbstech.spacex.app.ui.filterdrawer.FilterMenuItem
import com.corbstech.spacex.app.ui.list.RecyclerItem
import java.util.*

data class ViewSate(
    val filterMenuData: FilterMenuData = FilterMenuData(),
    val staticItems: List<RecyclerItem> = listOf(),
    val launchItems: List<LaunchItem> = listOf(),
    val events: List<Event> = emptyList(),
    val refreshing: Boolean = true,
    val noData: Boolean = false,
) {
    fun getItems() = listOf(staticItems, launchItems).flatten()
}

sealed class Action {
    class FilterMenuItemClicked(
        val filterMenuGroup: FilterMenuItem,
        val filterMenuItem: FilterMenuItem
    ) : Action()
    class LaunchItemLinkClicked(val launchItemLink: LaunchItemLink) : Action()
}

sealed class Event {
    val uniqueId: String = UUID.randomUUID().toString()
    class LaunchWebBrowser(val url: String) : Event()
}

sealed class SortOrder {
    object Asc : SortOrder()
    object Desc : SortOrder()
    object None : SortOrder()
}