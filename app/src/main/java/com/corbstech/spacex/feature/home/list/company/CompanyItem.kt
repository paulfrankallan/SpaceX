package com.corbstech.spacex.feature.home.list.company

import com.corbstech.spacex.shared.ui.list.RecyclerItem

data class CompanyItem(
    override val id: String = "",
    val info: String
) : RecyclerItem