package com.corbstech.spacex.feature.home.list.launch

import com.corbstech.spacex.shared.ui.list.RecyclerItem
import com.corbstech.spacex.shared.ui.list.RecyclerItemClicked

data class LaunchItem(
    override val id: String = "",
    val mission: String?,
    val date: String?,
    val year: Int?,
    val rocket: String?,
    val daysLabelAndValue: Pair<String, String>?,
    val patchImage: String? = null,
    val successImage: Int? = null,
    val links: List<LaunchItemLink>,
) : RecyclerItem, RecyclerItemClicked