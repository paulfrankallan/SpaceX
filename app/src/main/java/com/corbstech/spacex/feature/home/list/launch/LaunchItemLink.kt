package com.corbstech.spacex.feature.home.list.launch

import com.corbstech.spacex.shared.ui.list.RecyclerItemClicked

data class LaunchItemLink(
    val title: String,
    val url: String?
): RecyclerItemClicked