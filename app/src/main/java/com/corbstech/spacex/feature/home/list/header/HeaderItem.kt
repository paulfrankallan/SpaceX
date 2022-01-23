package com.corbstech.spacex.feature.home.list.header

import com.corbstech.spacex.shared.ui.list.RecyclerItem

data class HeaderItem(
    override val id: Long,
    val title: String
) : RecyclerItem