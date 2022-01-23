package com.corbstech.spacex.feature.list.company

import com.corbstech.spacex.app.ui.list.RecyclerItem

data class CompanyItem(
    override val id: Long,
    val info: String
) : RecyclerItem