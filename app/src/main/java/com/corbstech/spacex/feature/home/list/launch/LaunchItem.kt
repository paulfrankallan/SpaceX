package com.corbstech.spacex.feature.home.list.launch

import com.corbstech.spacex.shared.ui.list.RecyclerItem

data class LaunchItem(
    override val id: String = "",
    val mission: String?,
    val date: String?,
    val year: Int?,
    val rocket: String?,
    val daysLabelAndValue: Pair<String, String>?,
    var patchImage: String? = null,
    var successImage: Int? = null
) : RecyclerItem