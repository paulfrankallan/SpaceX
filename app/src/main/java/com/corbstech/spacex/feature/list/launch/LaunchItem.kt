package com.corbstech.spacex.feature.list.launch

import com.corbstech.spacex.app.ui.list.RecyclerItem
import com.corbstech.spacex.app.ui.list.Clickable

data class LaunchItem(
    override val id: Long,
    val mission: String?,
    val date: String?,
    val year: Int?,
    val rocket: String?,
    val daysLabelAndValue: Pair<String, String>?,
    val patchImage: String? = null,
    val successImage: Int? = null,
    val success: Boolean? = null,
    val links: List<LaunchItemLink>,
) : RecyclerItem, Clickable