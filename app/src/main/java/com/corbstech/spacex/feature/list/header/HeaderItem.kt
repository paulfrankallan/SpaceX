package com.corbstech.spacex.feature.list.header

import com.corbstech.spacex.app.ui.list.RecyclerItem

data class HeaderItem(
    override val id: Long,
    val title: String
) : RecyclerItem