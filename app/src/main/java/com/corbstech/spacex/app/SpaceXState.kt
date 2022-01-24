package com.corbstech.spacex.app

import com.corbstech.spacex.app.ui.list.RecyclerItem
import com.corbstech.spacex.feature.filtermenu.FilterMenuItem
import com.corbstech.spacex.feature.filtermenu.FilterMenuState
import com.corbstech.spacex.feature.list.launch.LaunchItem
import com.corbstech.spacex.feature.list.launch.LaunchItemLink
import java.util.*

data class ViewSate(
    val filterMenuState: FilterMenuState = FilterMenuState(),
    val staticItems: List<RecyclerItem> = listOf(),
    val launchItems: List<LaunchItem> = listOf(),
    val events: List<Event> = emptyList(),
    val refreshing: Boolean = false,
    val noData: Boolean = false,
) {
    fun getItems() = listOf(staticItems, launchItems).flatten()
}

sealed class Action {
    class FilterMenuItemClicked(
        val filterMenuGroup: FilterMenuItem,
        val filterMenuItem: FilterMenuItem
    ) : Action()

    class LaunchItemLinkClicked(
        val uniqueId: String = UUID.randomUUID().toString(),
        val launchItemLink: LaunchItemLink
    ) : Action()
}

sealed class Event(open val uniqueId: String) {
    class LaunchWebBrowser(
        val url: String,
        override var uniqueId: String
    ) : Event(uniqueId)
}